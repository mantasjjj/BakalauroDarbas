package vu.bakalauras.simulation.model.customer;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = {CascadeType.ALL})
    public List<CustomerCriteria> customerCriteria;
}
