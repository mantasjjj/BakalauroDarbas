package vu.bakalauras.simulation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import vu.bakalauras.simulation.model.SimulationRequest;
import vu.bakalauras.simulation.model.customer.Customer;
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

        for (Customer customer : customers) {
            shops = simulationService.simulatePurchaseProcess(shops, customer);
        }

        model.put("shops", shops);
        model.put("numberOfDays", simulateDays);
//        model.put("numberOfCustomers", numberOfCustomers);
        return "simulation";
    }

    @GetMapping("/simulation")
    public String simulateDays(@ModelAttribute("simulationRequest") SimulationRequest simulationRequest) {
        return "redirect:/simulation/" + simulationRequest.simulateDays;
    }

}
