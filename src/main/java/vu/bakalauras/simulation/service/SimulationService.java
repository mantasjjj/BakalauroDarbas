package vu.bakalauras.simulation.service;

import org.springframework.stereotype.Service;
import vu.bakalauras.simulation.Category;
import vu.bakalauras.simulation.model.customer.CriteriaImportance;
import vu.bakalauras.simulation.model.customer.Customer;
import vu.bakalauras.simulation.model.customer.CustomerCriteria;
import vu.bakalauras.simulation.model.seller.Seller;
import vu.bakalauras.simulation.model.shop.CriteriaWeight;
import vu.bakalauras.simulation.model.shop.Shop;
import vu.bakalauras.simulation.model.shop.ShopCriteria;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimulationService {

    public List<Shop> simulatePurchaseProcess(List<Shop> shops, Customer customer) {
        shops = resetShopScores(shops);
        Shop chosenShop = simulateShopChoice(shops, customer);

        for (Shop shop : shops) {
            if (shop.name.equals(chosenShop.name)) {
                int successfulPurchase = simulatePurchase(chosenShop.score);
                shop.totalSales += successfulPurchase;
                shop.dailySales += successfulPurchase;
                shop.sellers = simulateSellerChoice(shop.sellers, customer.customerCriteria);
            }
        }
        return shops;
    }

    public Shop simulateShopChoice(List<Shop> shops, Customer customer) {
        shops.forEach(shop -> shop.score = getShopScore(shop.shopCriteria, customer.customerCriteria, CriteriaImportance.MANDATORY, shop.bankrupt));
        double shopsMaxScore = getMaxShopScore(shops);

        List<Shop> chosenShops = shops.stream().filter(s -> s.score == shopsMaxScore).collect(Collectors.toList());

        if (chosenShops.size() > 1) {
            chosenShops.forEach(shop -> shop.score += getShopScore(shop.shopCriteria, customer.customerCriteria, CriteriaImportance.OPTIONAL, shop.bankrupt));

            double chosenShopsMaxScore = getMaxShopScore(chosenShops);
            return chosenShops.stream().filter(shop -> shop.score == chosenShopsMaxScore).findFirst().orElse(null);
        } else {
            return chosenShops.get(0);
        }
    }

    public double getShopScore(List<ShopCriteria> shopCriteria, List<CustomerCriteria> customerCriteria,
                               CriteriaImportance criteriaImportance, boolean shopBankrupt) {
        if (!shopBankrupt) {
            List<CustomerCriteria> filteredCustomerCriteria = customerCriteria
                    .stream()
                    .filter(c -> c.criteriaImportance == criteriaImportance)
                    .collect(Collectors.toList());

            double totalShopCriteriaScore = getTotalShopScore(shopCriteria);
            double shopScore = 0;
            for (CustomerCriteria custCriteria : filteredCustomerCriteria) {
                List<ShopCriteria> filteredCriteriaByCategory = shopCriteria
                        .stream()
                        .filter(s -> s.categories.contains(custCriteria.category)
                                && s.criteriaWeight != CriteriaWeight.HIGHEST && s.criteriaWeight != CriteriaWeight.AD)
                        .collect(Collectors.toList());

                shopScore += getCriteriaScore(filteredCriteriaByCategory, totalShopCriteriaScore);
            }
            return shopScore;
        }
        return 0;
    }

    public static double getTotalShopScore(List<ShopCriteria> shopCriteria) {
        return shopCriteria.stream()
                .filter(s -> s.criteriaWeight != CriteriaWeight.HIGHEST && s.criteriaWeight != CriteriaWeight.AD)
                .map(s -> s.criteriaWeight.rating)
                .reduce(0, Integer::sum);
    }

    public static double getCriteriaScore(List<ShopCriteria> filteredShopCriteria, double totalCriteriaScore) {
        double totalScore = filteredShopCriteria.stream().map(ShopCriteria::getCriteriaWeightRating).reduce(0, Integer::sum);
        return totalScore / totalCriteriaScore;
    }

    public int simulatePurchase(double shopScore) {
        if (shopScore + Math.random() > 2.4) {
            return 1;
        }
        return 0;
    }

    public List<Seller> simulateSellerChoice(List<Seller> sellers, List<CustomerCriteria> customerCriteria) {
        sellers.forEach(s -> s.criteriaMatch = 0);
        List<Seller> highestRankedSellers = sellers.stream().filter(s -> !s.bankrupt).limit(7).collect(Collectors.toList()); //7, nes Amazon rodo tik 7
        Seller chosenSeller = null;

        //todo: improve with mandatory and optional
        for (Seller seller : highestRankedSellers) {
            for (Category sellerCategory : seller.criteria) {
                seller.criteriaMatch += (int) customerCriteria.stream().filter(c -> c.category == sellerCategory).count();
            }
        }

        chosenSeller = highestRankedSellers.stream().max(Comparator.comparingInt(Seller::getCriteriaMatch)).get();
        for (Seller seller : sellers) {
            if (chosenSeller.getId() == seller.getId()) {
                seller.totalSales++;
                seller.dailySales++;
            }
        }
        return sellers;
    }

    public List<Shop> updateBankruptSellers(List<Shop> shops) {
        for (Shop shop : shops) {
            for (Seller seller : shop.sellers) {
                if (seller.dailySales == 0) {
                    seller.daysWithoutSale++;
                }
                if (seller.daysWithoutSale >= seller.maxDaysWithoutSale) {
                    seller.bankrupt = true;
                }
            }
            if (shop.dailySales < shop.minimumDailySales) {
                shop.daysWithLessThanAllowedSales++;
            }
            if (shop.daysWithLessThanAllowedSales >= shop.maxDaysWithLessThanAllowedSales) {
                shop.bankrupt = true;
            }
        }
        shops.forEach(shop -> {
            shop.sellers.forEach(seller -> seller.dailySales = 0);
            shop.dailySales = 0;
        });
        return shops;
    }

    public List<Shop> resetShopScores(List<Shop> shops) {
        shops.forEach(shop -> shop.score = 0);
        return shops;
    }

    private double getMaxShopScore(List<Shop> shops) {
        double max = -1;
        for (Shop shop : shops) {
            if (shop.score > max) {
                max = shop.score;
            }
        }
        return max;
    }

}
