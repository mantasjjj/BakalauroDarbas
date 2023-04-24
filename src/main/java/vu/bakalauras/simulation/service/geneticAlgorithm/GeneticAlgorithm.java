package vu.bakalauras.simulation.service.geneticAlgorithm;

import vu.bakalauras.simulation.Category;
import vu.bakalauras.simulation.model.shop.CriteriaWeight;
import vu.bakalauras.simulation.model.shop.Shop;
import vu.bakalauras.simulation.model.shop.ShopCriteria;
import vu.bakalauras.simulation.service.SimulationService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static vu.bakalauras.simulation.service.geneticAlgorithm.Population.getTotalScore;

public class GeneticAlgorithm {

    Population population = new Population();
    SimulationService simulationService = new SimulationService();
    Shop fittest;
    Shop secondFittest;
    int generationCount = 0;

    public static void main(String[] args) {
        Random random = new Random();

        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();

        geneticAlgorithm.population.initializeInitialPopulation();
        geneticAlgorithm.population.initializeCustomers();

        geneticAlgorithm.population.calculateFitness();

        System.out.println("Generation: " + geneticAlgorithm.generationCount + " Fittest: " + geneticAlgorithm.population.fittest);

        while (geneticAlgorithm.population.fittest < 160 || !isMinimumCriteriaReached(geneticAlgorithm.population.individuals.get(0))) {
            ++geneticAlgorithm.generationCount;

            geneticAlgorithm.selection();

            geneticAlgorithm.crossover();

            if (random.nextInt() % 7 < 5) {
                geneticAlgorithm.mutation();
            }

            geneticAlgorithm.addFittestOffspring();

            geneticAlgorithm.population.calculateFitness();

            System.out.println("Generation: " + geneticAlgorithm.generationCount + " Fittest: " + geneticAlgorithm.population.fittest
            + " Criteria size: " + geneticAlgorithm.population.individuals.get(0).shopCriteria.stream().filter(s -> s.criteriaWeight != null).count());
        }

        System.out.println("\nSolution found in generation " + geneticAlgorithm.generationCount);
        System.out.println("Fitness: " + geneticAlgorithm.population.getFittest().score);
        System.out.println("Criteria model:");

        List<ShopCriteria> finalShopCriteria = geneticAlgorithm.population.getFittest().shopCriteria
                .stream()
                .filter(s -> s.criteriaWeight != null)
                .collect(Collectors.toList());
        for (ShopCriteria shopCriteria : finalShopCriteria) {
            StringBuilder categoriesOutput = new StringBuilder();
            for (Category category : shopCriteria.categories) {
                categoriesOutput.append("Category.").append(category).append(", ");
            }
            System.out.println("shopCriteria.add(new ShopCriteria(\"" + shopCriteria.name +
                    "\", Arrays.asList(" + categoriesOutput + "), CriteriaWeight." + shopCriteria.criteriaWeight + "));");
        }
    }

    public void selection() {

        fittest = population.getFittest();

        secondFittest = population.getSecondFittest();
    }

    public void crossover() {
        Random random = new Random();
        List<ShopCriteria> shopCriteriaForFittest = new ArrayList<>();
        List<ShopCriteria> shopCriteriaForSecondFittest = new ArrayList<>();

        int size = population.individuals.get(0).shopCriteria.size();
        int crossOverPoint = size / 4;
        crossOverPoint = size - crossOverPoint;

//        int crossOverPoint = random.nextInt(size);

        for (int i = 0; i < size; i++) {
            if (i <= crossOverPoint) {
                if (secondFittest.shopCriteria.size() > i) {
                    shopCriteriaForFittest.add(secondFittest.shopCriteria.get(i));
                }
                if (fittest.shopCriteria.size() > i) {
                    shopCriteriaForSecondFittest.add(fittest.shopCriteria.get(i));
                }
            } else {
                if (fittest.shopCriteria.size() > i) {
                    shopCriteriaForFittest.add(fittest.shopCriteria.get(i));
                }
                if (secondFittest.shopCriteria.size() > i) {
                    shopCriteriaForSecondFittest.add(secondFittest.shopCriteria.get(i));
                }
            }
        }

        fittest.shopCriteria = shopCriteriaForFittest;
        secondFittest.shopCriteria = shopCriteriaForSecondFittest;
    }

    public void mutation() {
        Random rn = new Random();
        List<CriteriaWeight> criteriaWeights = Arrays.asList(CriteriaWeight.HIGH, CriteriaWeight.MEDIUM, CriteriaWeight.LOW);

        int mutationPoint = rn.nextInt(population.individuals.get(0).shopCriteria.size());
        int randomWeight = rn.nextInt(criteriaWeights.size());

        population.individuals.get(0).shopCriteria.get(mutationPoint).criteriaWeight = criteriaWeights.get(randomWeight);

        mutationPoint = rn.nextInt(population.individuals.get(0).shopCriteria.size());
        randomWeight = rn.nextInt(criteriaWeights.size());

        population.individuals.get(0).shopCriteria.get(mutationPoint).criteriaWeight = criteriaWeights.get(randomWeight);
    }

    public Shop getFittestOffspring() {
        if (fittest.score > secondFittest.score) {
            return fittest;
        }
        return secondFittest;
    }


    public void addFittestOffspring() {

        fittest.score = getTotalScore(population.customers, fittest, simulationService);
        secondFittest.score = getTotalScore(population.customers, secondFittest, simulationService);

        int leastFittestIndex = population.getLeastFittestIndex();

        population.individuals.remove(leastFittestIndex);
        population.individuals.add(getFittestOffspring());
    }

    private static boolean isMinimumCriteriaReached(Shop shop) {
        return shop.shopCriteria.stream().filter(s -> s.criteriaWeight != null).count() > 25;
    }

}
