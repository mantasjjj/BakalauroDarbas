package vu.bakalauras.simulation.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vu.bakalauras.simulation.model.customer.Customer;
import vu.bakalauras.simulation.model.shop.Shop;
import vu.bakalauras.simulation.service.CustomerService;
import vu.bakalauras.simulation.service.ShopService;

import java.util.List;

@Component
public class AbstractCommandLineRunner implements CommandLineRunner {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ShopService shopService;
    @Override
    public void run(String... args) {

        List<Customer> customers = fillCustomers();
        customers.forEach(c -> customerService.add(c));

        List<Shop> eshops = fillShops();
        eshops.forEach(e -> shopService.add(e));
    }

    public List<Customer> fillCustomers() {
        return null;
    }

    public List<Shop> fillShops() {
        return null;
    }
}
