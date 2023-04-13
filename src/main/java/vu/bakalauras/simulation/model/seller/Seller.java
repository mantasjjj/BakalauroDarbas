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
    public List<Category> focusZones;
    public double sellerScore;
    public int criteriaMatch;
    public int totalSales = 0;
    public int dailySales = 0;
    public int daysWithoutSale = 0;
    public int maxDaysWithoutSale = 10;
    public int productAmount;
    public boolean bankrupt;
    public boolean hasSeasonalProducts;
    public boolean isFrequentPromoter;

    public Seller() {

    }

    public Seller(List<Category> focusZones, boolean hasSeasonalProducts, boolean isFrequentPromoter, int productAmount) {
        this.focusZones = focusZones;
        this.hasSeasonalProducts = hasSeasonalProducts;
        this.isFrequentPromoter = isFrequentPromoter;
        this.productAmount = productAmount;
    }
}
