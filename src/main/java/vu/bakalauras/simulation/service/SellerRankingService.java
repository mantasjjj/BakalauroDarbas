package vu.bakalauras.simulation.service;

import org.springframework.stereotype.Service;
import vu.bakalauras.simulation.Category;
import vu.bakalauras.simulation.model.seller.Seller;
import vu.bakalauras.simulation.model.shop.CriteriaWeight;
import vu.bakalauras.simulation.model.shop.Shop;
import vu.bakalauras.simulation.model.shop.ShopCriteria;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static vu.bakalauras.simulation.service.SimulationService.getCriteriaScore;
import static vu.bakalauras.simulation.service.SimulationService.getTotalShopScore;

@Service
public class SellerRankingService {

    public List<Seller> getRankedSellers(List<ShopCriteria> shopCriteria, List<Seller> sellers) {
        sellers.forEach(seller -> seller.sellerScore = 0);
        double totalShopCriteriaScore = getTotalShopScore(shopCriteria);

        for (Seller seller : sellers) {
            double sellerScore = 0;
            for (Category category : seller.focusZones) {
                List<ShopCriteria> filteredCriteriaByCategory = shopCriteria
                        .stream()
                        .filter(s -> s.categories.contains(category)
                                && s.criteriaWeight != CriteriaWeight.HIGHEST && s.criteriaWeight != CriteriaWeight.AD)
                        .collect(Collectors.toList());

                sellerScore += getCriteriaScore(filteredCriteriaByCategory, totalShopCriteriaScore);
            }

            seller.sellerScore = sellerScore;
        }

        calculatePenaltyPointsForProducts(sellers);

        sellers = sellers
                .stream()
                .sorted(Comparator.comparingDouble(Seller::getSellerScore).reversed())
                .collect(Collectors.toList());

        return sellers;
    }

    private void calculatePenaltyPointsForProducts(List<Seller> sellers) {
        for (Seller seller : sellers) {
            if (seller.productAmount > 3) {
                seller.sellerScore -= (seller.productAmount - 3) * 0.65;
            }
        }
    }
}
