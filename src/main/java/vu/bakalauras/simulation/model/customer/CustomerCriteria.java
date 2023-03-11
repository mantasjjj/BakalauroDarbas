package vu.bakalauras.simulation.model.customer;

import lombok.Getter;
import lombok.Setter;
import vu.bakalauras.simulation.Category;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class CustomerCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    public String name;
    @Enumerated
    public Category category;
    @Enumerated
    public CriteriaImportance criteriaImportance;
}
