package vu.bakalauras.simulation.service;

import org.springframework.stereotype.Service;
import vu.bakalauras.simulation.Category;
import vu.bakalauras.simulation.model.customer.CriteriaImportance;
import vu.bakalauras.simulation.model.customer.Customer;
import vu.bakalauras.simulation.model.customer.CustomerCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CustomerGenerator {
    private static final Random RANDOM = new Random();

    public List<Customer> generateCustomerList() {
        return generateCustomerList(0);
    }

    public List<Customer> generateCustomerList(int size) {
        if (size == 0) {
            size = 1000;
        }

        List<Customer> customers = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            List<CustomerCriteria> customerCriteria = new ArrayList<>();
            customerCriteria.add(new CustomerCriteria(Category.SEARCH_RESULT_ACCURACY, CriteriaImportance.MANDATORY));

            while (customerCriteria.size() != 5) {
                Category[] categories = Category.values();
                Category randomCategory = categories[RANDOM.nextInt(categories.length)];
                CustomerCriteria randomCustomerCriteria = new CustomerCriteria(randomCategory, CriteriaImportance.MANDATORY);
                customerCriteria.add(randomCustomerCriteria);
            }
            customers.add(new Customer(customerCriteria));
        }

        return customers;
    }
}
