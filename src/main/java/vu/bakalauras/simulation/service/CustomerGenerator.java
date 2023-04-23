package vu.bakalauras.simulation.service;

import org.springframework.stereotype.Service;
import vu.bakalauras.simulation.Category;
import vu.bakalauras.simulation.model.customer.CriteriaImportance;
import vu.bakalauras.simulation.model.customer.Customer;
import vu.bakalauras.simulation.model.customer.CustomerCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CustomerGenerator {

    public List<Customer> generateCustomerList() {
        return generateCustomerList(0, 0);
    }

    public List<Customer> generateCustomerList(int size, long seed) {
        Random random;
        if (seed != 0) {
            random = new Random();
            random.setSeed(seed);
        } else {
            random = new Random();
        }

        if (size == 0) {
            size = 1000;
        }

        List<Customer> customers = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            List<CustomerCriteria> customerCriteria = new ArrayList<>();
            customerCriteria.add(new CustomerCriteria(Category.SEARCH_RESULT_ACCURACY, CriteriaImportance.MANDATORY));

            while (customerCriteria.size() != 5) {
                Category[] categories = Category.values();
                Category randomCategory = categories[random.nextInt(categories.length)];
                CustomerCriteria randomCustomerCriteria = new CustomerCriteria(randomCategory, CriteriaImportance.MANDATORY);
                customerCriteria.add(randomCustomerCriteria);
            }
            customers.add(new Customer(customerCriteria));
        }

        return customers;
    }
}
