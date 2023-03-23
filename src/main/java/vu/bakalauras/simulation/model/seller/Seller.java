package vu.bakalauras.simulation.model.seller;

import lombok.Getter;
import lombok.Setter;
import vu.bakalauras.simulation.Category;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ElementCollection
    public List<Category> criteria;
    public double sellerScore;
    public int totalSales = 0;
    public boolean bankrupt;

    public Seller() {

    }

    public Seller(List<Category> criteria) {
        this.criteria = criteria;
    }
}
