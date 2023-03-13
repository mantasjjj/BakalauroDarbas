package vu.bakalauras.simulation.model.shop;

import lombok.Getter;
import lombok.Setter;
import vu.bakalauras.simulation.Category;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class ShopCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public String name;
    @ElementCollection
    public List<Category> categories;
    @Enumerated
    public CriteriaWeight criteriaWeight;

    public ShopCriteria() {
    }

    public ShopCriteria(String name, List<Category> categories, CriteriaWeight criteriaWeight) {
        this.name = name;
        this.categories = categories;
        this.criteriaWeight = criteriaWeight;
    }

    public int getCriteriaWeightRating() {
        return criteriaWeight.rating;
    }
}
