package vu.bakalauras.simulation.model.shop;

public enum CriteriaWeight {

    HIGHEST(1000),
    HIGH(30),
    MEDIUM(10),
    LOW(2),
    NONE(0),
    AD(500);

    public final int rating;

    CriteriaWeight(int rating) {
        this.rating = rating;
    }
}
