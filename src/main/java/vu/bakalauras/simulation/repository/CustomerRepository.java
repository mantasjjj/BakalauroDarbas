package vu.bakalauras.simulation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vu.bakalauras.simulation.model.customer.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
