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
    public int highCriteriaCount;
    @OneToOne(cascade = {CascadeType.ALL})
    public Owner owner;
    @OneToMany(cascade = {CascadeType.ALL})
    public List<Seller> sellers;
    public int totalSales;
    public double score;
    public boolean mostSales;
}
