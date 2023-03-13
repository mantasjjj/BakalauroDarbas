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
    @Enumerated
    public Category category;
    @Enumerated
    public CriteriaImportance criteriaImportance;

    public CustomerCriteria() {

    }

    public CustomerCriteria(Category category, CriteriaImportance criteriaImportance) {
        this.category = category;
        this.criteriaImportance = criteriaImportance;
    }
}
