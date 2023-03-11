package vu.bakalauras.simulation.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class SimulationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    public Integer simulateDays;

    public SimulationRequest(Integer simulateDays) {
        this.simulateDays = simulateDays;
    }

    public SimulationRequest() {
    }
}
