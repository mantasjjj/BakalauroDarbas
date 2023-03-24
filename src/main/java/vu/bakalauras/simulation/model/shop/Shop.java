package vu.bakalauras.simulation.model.shop;

import lombok.Getter;
import lombok.Setter;
import vu.bakalauras.simulation.model.owner.Owner;
import vu.bakalauras.simulation.model.seller.Seller;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public String name;
    @OneToMany(cascade = {CascadeType.ALL})
    public List<ShopCriteria> shopCriteria;
    @OneToOne(cascade = {CascadeType.ALL})
    public Owner owner;
    @OneToMany(cascade = {CascadeType.ALL})
    public List<Seller> sellers;
    public int totalSales = 0;
    public double score;
    public boolean mostSales;
    public double bankruptSellers;

    public Shop() {
    }

    public Shop(String name, List<ShopCriteria> shopCriteria) {
        this.name = name;
        this.shopCriteria = shopCriteria;
    }

    public Shop(String name, List<ShopCriteria> shopCriteria, List<Seller> sellers) {
        this.name = name;
        this.shopCriteria = shopCriteria;
        this.sellers = sellers;
    }
}
