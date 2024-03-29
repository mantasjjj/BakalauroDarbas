package vu.bakalauras.simulation.model.owner;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    public int maxDaysWithLessThanAllowedSales;
    public int minimumDailySales;

    public Owner() {
    }

    public Owner(int maxDaysWithLessThanAllowedSales, int minimumDailySales) {
        this.maxDaysWithLessThanAllowedSales = maxDaysWithLessThanAllowedSales;
        this.minimumDailySales = minimumDailySales;
    }
}
