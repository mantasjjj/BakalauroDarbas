package vu.bakalauras.simulation.model.shop;

import vu.bakalauras.simulation.model.owner.Owner;
import vu.bakalauras.simulation.model.seller.Seller;

import java.util.List;

public class Shop {
    public String name;
    public List<ShopCriteria> criteria;
    public Owner owner;
    public List<Seller> sellers;
    public int totalSales;
    public double score;
}
