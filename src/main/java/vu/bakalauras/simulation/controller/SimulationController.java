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

import java.util.List;

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
            for (Customer customer : customers) {
                shops = simulationService.simulatePurchaseProcess(shops, customer);
            }
            numberOfCustomers += 1000;
            shops = simulationService.updateBankruptSellers(shops);
        }


        updateShops(shops);

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
            shops.get(i).bankruptSellers = (bankruptSellersCount * 100) / shops.get(i).sellers.size();
        }

        shops.get(index).mostSales = true;
    }
}
