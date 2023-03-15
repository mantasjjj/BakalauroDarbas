package vu.bakalauras.simulation.service;

import org.springframework.stereotype.Service;
import vu.bakalauras.simulation.model.customer.CriteriaImportance;
import vu.bakalauras.simulation.model.customer.Customer;
import vu.bakalauras.simulation.model.customer.CustomerCriteria;
import vu.bakalauras.simulation.model.shop.CriteriaWeight;
import vu.bakalauras.simulation.model.shop.Shop;
import vu.bakalauras.simulation.model.shop.ShopCriteria;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimulationService {

    public List<Shop> simulatePurchaseProcess(List<Shop> shops, Customer customer) {
        shops = resetShopScores(shops);
        Shop chosenShop = simulateShopChoice(shops, customer);

        for (Shop shop : shops) {
            if (shop.name.equals(chosenShop.name)) {
                shop.totalSales += simulatePurchase(chosenShop.score);
            }
        }
        return shops;
    }

    public Shop simulateShopChoice(List<Shop> shops, Customer customer) {
        shops.forEach(shop -> shop.score = getShopScore(shop.shopCriteria, customer.customerCriteria, CriteriaImportance.MANDATORY));
        double shopsMaxScore = getMax(shops);

        List<Shop> chosenShops = shops.stream().filter(s -> s.score == shopsMaxScore).collect(Collectors.toList());

        if (chosenShops.size() > 1) {
            chosenShops.forEach(shop -> shop.score += getShopScore(shop.shopCriteria, customer.customerCriteria, CriteriaImportance.OPTIONAL));

            double chosenShopsMaxScore = getMax(chosenShops);
            return chosenShops.stream().filter(shop -> shop.score == chosenShopsMaxScore).findFirst().orElse(null);
        } else {
            return chosenShops.get(0);
        }
    }

    public double getShopScore(List<ShopCriteria> shopCriteria, List<CustomerCriteria> customerCriteria,
                               CriteriaImportance criteriaImportance) {
        List<CustomerCriteria> filteredCustomerCriteria = customerCriteria
                .stream()
                .filter(c -> c.criteriaImportance == criteriaImportance)
                .collect(Collectors.toList());

        double totalShopCriteriaScore = shopCriteria.stream()
                .filter(s -> s.criteriaWeight != CriteriaWeight.HIGHEST && s.criteriaWeight != CriteriaWeight.AD)
                .map(s -> s.criteriaWeight.rating)
                .reduce(0, Integer::sum);
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

    public double getCriteriaScore(List<ShopCriteria> filteredShopCriteria, double totalCriteriaScore) {
        double totalScore = filteredShopCriteria.stream().map(ShopCriteria::getCriteriaWeightRating).reduce(0, Integer::sum);
        return totalScore / totalCriteriaScore;
    }

    public int simulatePurchase(double shopScore) {
        if (shopScore + Math.random() > 2.4) {
            return 1;
        }
        return 0;
    }

    public List<Shop> resetShopScores(List<Shop> shops) {
        shops.forEach(shop -> shop.score = 0);
        return shops;
    }

    private double getMax(List<Shop> shops) {
        double max = -1;
        for (Shop shop : shops) {
            if (shop.score > max) {
                max = shop.score;
            }
        }
        return max;
    }
}
