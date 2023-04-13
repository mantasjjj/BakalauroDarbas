package vu.bakalauras.simulation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import vu.bakalauras.simulation.model.SimulationRequest;
import vu.bakalauras.simulation.model.customer.Customer;
import vu.bakalauras.simulation.model.seller.Seller;
import vu.bakalauras.simulation.model.shop.Shop;
import vu.bakalauras.simulation.service.CustomerService;
import vu.bakalauras.simulation.service.ShopService;
import vu.bakalauras.simulation.service.SimulationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SimulationController {

    @Autowired
    SimulationService simulationService;
    @Autowired
    ShopService shopService;
    @Autowired
    CustomerService customerService;

    @GetMapping("/simulation/{simulateDays}")
    public String showSimulationPage(@PathVariable Integer simulateDays,
                                     ModelMap model) {
        model.addAttribute("simulationRequest", new SimulationRequest());
        List<Customer> customers = customerService.findAll();
        List<Shop> shops = shopService.findAll();
        int numberOfCustomers = 0;

        for (int i = 0; i < simulateDays; i++) {
            increaseSellerRating(shops, i, true);
            increaseSellerRating(shops, i, false);
            for (int j = 0; j < 10; j++) {
                for (Customer customer : customers) {
                    shops = simulationService.simulatePurchaseProcess(shops, customer);
                }
            }
            decreaseSellerRating(shops, i, true);
            decreaseSellerRating(shops, i, false);
            numberOfCustomers += 10000;
            shops = simulationService.updateBankruptSellers(shops);
        }

        updateShops(shops);
        shops = shops.stream().sorted(Comparator.comparingInt(Shop::getTotalSales).reversed()).collect(Collectors.toList());

        model.put("shops", shops);
        model.put("numberOfDays", simulateDays);
        model.put("numberOfCustomers", numberOfCustomers);
        return "simulation";
    }

    @GetMapping("/simulation")
    public String simulateDays(@ModelAttribute("simulationRequest") SimulationRequest simulationRequest) {
        return "redirect:/simulation/" + 0;
    }

    private void updateShops(List<Shop> shops) {
        double max = -1;
        int index = 0;
        for (int i = 0; i < shops.size(); i++) {
            double bankruptSellersCount = 0;
            if (shops.get(i).totalSales > max) {
                max = shops.get(i).totalSales;
                index = i;
            }
            for (Seller seller : shops.get(i).sellers) {
                bankruptSellersCount += seller.bankrupt ? 1 : 0;
            }
            BigDecimal bankruptSellerPercentage = (BigDecimal.valueOf(bankruptSellersCount).multiply(BigDecimal.valueOf(100)))
                    .divide(BigDecimal.valueOf(shops.get(i).sellers.size()), RoundingMode.HALF_UP);
            shops.get(i).bankruptSellers = bankruptSellerPercentage.setScale(2, RoundingMode.HALF_UP);
            shops.get(i).generatedRevenue = shops.get(i).averageProductPrice.multiply(BigDecimal.valueOf(shops.get(i).totalSales));
        }

        shops.get(index).mostSales = true;
    }

    private void increaseSellerRating(List<Shop> shops, int day, boolean promoter) {
        for (Shop shop: shops) {
            for (Seller seller: shop.sellers) {
                if (promoter) {
                    if (seller.isFrequentPromoter && day % 5 == 0) {
                        seller.sellerScore += 0.4;
                    }
                } else {
                    if (seller.hasSeasonalProducts && day % 5 == 0) {
                        seller.sellerScore += 0.4;
                    }
                }
            }
        }
    }

    private void decreaseSellerRating(List<Shop> shops, int day, boolean promoter) {
        for (Shop shop: shops) {
            for (Seller seller: shop.sellers) {
                if (promoter) {
                    if (seller.isFrequentPromoter && day % 10 == 0) {
                        seller.sellerScore -= 0.4;
                    }
                } else {
                    if (seller.hasSeasonalProducts && day % 5 == 0) {
                        seller.sellerScore -= 0.4;
                    }
                }
            }
        }
    }
}
