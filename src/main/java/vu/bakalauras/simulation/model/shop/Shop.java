package vu.bakalauras.simulation.model.shop;

import lombok.Getter;
import lombok.Setter;
import vu.bakalauras.simulation.model.owner.Owner;
import vu.bakalauras.simulation.model.seller.Seller;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    public String shopName;
    public String searchEngineName;
    @OneToMany(cascade = {CascadeType.ALL})
    public List<ShopCriteria> shopCriteria;
    @OneToOne(cascade = {CascadeType.ALL})
    public Owner owner;
    @OneToMany(cascade = {CascadeType.ALL})
    public List<Seller> sellers;
    public int totalSales = 0;
    public int dailySales = 0;
    public int minimumDailySales = 0;
    public int highestSellerPosition = -1;
    public BigDecimal generatedRevenue = BigDecimal.ZERO;
    public BigDecimal averageProductPrice;
    public double score;
    public boolean mostSales;
    public BigDecimal bankruptSellers;
    public int daysWithLessThanAllowedSales = 0;
    public int maxDaysWithLessThanAllowedSales;
    public boolean bankrupt = false;

    public Shop() {
    }

    public Shop(String shopName, String searchEngineName, List<ShopCriteria> shopCriteria, List<Seller> sellers, BigDecimal averageProductPrice, Owner owner) {
        this.shopName = shopName;
        this.searchEngineName = searchEngineName;
        this.shopCriteria = shopCriteria;
        this.sellers = sellers;
        this.averageProductPrice = averageProductPrice;
        this.maxDaysWithLessThanAllowedSales = owner.maxDaysWithLessThanAllowedSales;
        this.minimumDailySales = owner.minimumDailySales;
    }
}
