package vu.bakalauras.simulation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vu.bakalauras.simulation.model.customer.Customer;
import vu.bakalauras.simulation.repository.CustomerRepository;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> findAll() {
        return (List<Customer>) customerRepository.findAll();
    }

    public Customer findById(int id) {
        return customerRepository.findById(id).orElse(null);
    }

    public Customer add(Customer item) {
        return customerRepository.save(item);
    }

    public void update(Customer item) {
        customerRepository.save(item);
    }

    public void delete(Customer item) {
        customerRepository.delete(item);
    }

    public void deleteById(int id) {
        customerRepository.deleteById(id);
    }
}

