package vu.bakalauras.simulation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vu.bakalauras.simulation.model.shop.Shop;
import vu.bakalauras.simulation.repository.ShopRepository;

import java.util.List;

@Service
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;

    public List<Shop> findAll() {
        return (List<Shop>) shopRepository.findAll();
    }

    public Shop findById(int id) {
        return shopRepository.findById(id).orElse(null);
    }

    public Shop add(Shop item) {
        return shopRepository.save(item);
    }

    public void update(Shop item) {
        shopRepository.save(item);
    }

    public void delete(Shop item) {
        shopRepository.delete(item);
    }

    public void deleteById(int id) {
        shopRepository.deleteById(id);
    }
}