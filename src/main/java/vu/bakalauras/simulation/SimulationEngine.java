package vu.bakalauras.simulation;

import vu.bakalauras.simulation.model.customer.Customer;
import vu.bakalauras.simulation.model.customer.CustomerCriteria;
import vu.bakalauras.simulation.model.shop.Shop;
import vu.bakalauras.simulation.model.shop.ShopCriteria;

import java.util.List;
import java.util.stream.Collectors;

public class SimulationEngine {

    public void simulatePurchaseProcess(List<Shop> shops, Customer customer) {
        resetShopScores(shops);
        Shop chosenShop = simulateShopChoice(shops, customer);
    }

    public Shop simulateShopChoice(List<Shop> shops, Customer customer) {
        shops.forEach(shop -> shop.score = getShopScore(shop.criteria, customer.criteria, true));

        double shopsMaxScore = getMax(shops);
        List<Shop> chosenShops = shops.stream().filter(s -> s.score == shopsMaxScore).collect(Collectors.toList());

        if (chosenShops.size() > 1) {
            chosenShops.forEach(shop -> shop.score = getShopScore(shop.criteria, customer.criteria, false));

            double chosenShopsMaxScore = getMax(chosenShops);
            return chosenShops.stream().filter(shop -> shop.score == chosenShopsMaxScore).findFirst().orElse(null);
        } else {
            return chosenShops.get(0);
        }
    }

    public double getShopScore(List<ShopCriteria> shopCriteria, List<CustomerCriteria> customerCriteria, boolean simulateMandatory) {
        return 1;
    }

    public double getCriteriaScore() {
        return 0;
    }

    public int simulatePurchase() {
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
