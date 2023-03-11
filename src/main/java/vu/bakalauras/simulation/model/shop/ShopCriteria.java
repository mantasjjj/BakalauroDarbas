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
    public boolean positiveWeight;

    public int getCriteriaWeightRating(int highCriteriaCount) {
        if (criteriaWeight != CriteriaWeight.AD) {
            int multiplier = positiveWeight ? 1 : -1;
            return criteriaWeight.rating * multiplier;
        } else {
            return highCriteriaCount * criteriaWeight.rating;
        }
    }
}
