package vu.bakalauras.simulation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vu.bakalauras.simulation.model.shop.Shop;

public interface ShopRepository extends JpaRepository<Shop, Integer> {
}
