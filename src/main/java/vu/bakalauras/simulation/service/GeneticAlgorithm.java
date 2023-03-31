package vu.bakalauras.simulation.service;

import vu.bakalauras.simulation.Category;
import vu.bakalauras.simulation.model.customer.CriteriaImportance;
import vu.bakalauras.simulation.model.customer.Customer;
import vu.bakalauras.simulation.model.owner.Owner;
import vu.bakalauras.simulation.model.shop.CriteriaWeight;
import vu.bakalauras.simulation.model.shop.Shop;
import vu.bakalauras.simulation.model.shop.ShopCriteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
        CustomerGenerator customerGenerator = new CustomerGenerator();
        SimulationService simulationService = new SimulationService();
        List<Customer> customerList = customerGenerator.generateCustomerList(100);
        Shop shop = geneticAlgorithm.fillNewShop(geneticAlgorithm);
        Shop mutatedShop;
        double totalScore = geneticAlgorithm.getNewTotalScore(customerList, shop, simulationService);
        double previousTotalScore = totalScore;

        while (totalScore < 170) {
            mutatedShop = geneticAlgorithm.mutateShopCriteria(shop);
            totalScore = geneticAlgorithm.getNewTotalScore(customerList, shop, simulationService);

            if (totalScore > previousTotalScore) {
                shop = mutatedShop;
            }
        }

        for (ShopCriteria shopCriteria : shop.shopCriteria) {
            StringBuilder categoriesOutput = new StringBuilder();
            for (Category category : shopCriteria.categories) {
                categoriesOutput.append("Category.").append(category).append(", ");
            }
            System.out.println("shopCriteria.add(new ShopCriteria(\"" + shopCriteria.name +
                    "\", Arrays.asList(" + categoriesOutput + "), CriteriaWeight." + shopCriteria.criteriaWeight + "));");
        }
    }

    public Shop fillNewShop(GeneticAlgorithm geneticAlgorithm) {
        List<ShopCriteria> randomlyFilledCriteria = new ArrayList<>();
        List<ShopCriteria> allCriteria = geneticAlgorithm.getAllPossibleValues();
        while (randomlyFilledCriteria.size() < 30) {
            ShopCriteria randomCriteria = allCriteria.get(RANDOM.nextInt(allCriteria.size()));
            if (randomlyFilledCriteria.stream().filter(s -> s.name.equals(randomCriteria.name)).findFirst().isEmpty()) {
                randomlyFilledCriteria.add(randomCriteria);
            }
        }
        return new Shop("GeneticAlgorithmShop", "GeneticAlgorithm",
                randomlyFilledCriteria, null, null, new Owner());
    }

    public Shop mutateShopCriteria(Shop shop) {
        //mutate
        List<ShopCriteria> possibleValues = getAllPossibleValues();
        ShopCriteria randomCriteria = possibleValues.get(RANDOM.nextInt(possibleValues.size()));
        boolean valueUnchanged = true;
        while (valueUnchanged) {
            ShopCriteria randomCriteria1 = randomCriteria;
            if (shop.shopCriteria.stream().filter(s -> s.name.equals(randomCriteria1.name)).findFirst().isEmpty()) {
                valueUnchanged = false;
                shop.shopCriteria.remove(RANDOM.nextInt(shop.shopCriteria.size()));
                shop.shopCriteria.add(randomCriteria);
            } else {
                randomCriteria = possibleValues.get(RANDOM.nextInt(possibleValues.size()));
            }
        }
        return shop;
    }

    public double getNewTotalScore(List<Customer> customers, Shop shop, SimulationService simulationService) {
        double totalScore = 0;

        for (Customer customer : customers) {
            totalScore += simulationService.getShopScore(shop.shopCriteria, customer.customerCriteria, CriteriaImportance.MANDATORY, false);
        }

        return totalScore;
    }

    public List<ShopCriteria> fillInitialCriteria() {
        List<ShopCriteria> initialCriteria = new ArrayList<>();

        initialCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGHEST));

        initialCriteria.add(new ShopCriteria("Produkto reklama",
                Arrays.asList(), CriteriaWeight.AD));

        initialCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        initialCriteria.add(new ShopCriteria("Optimali produkto kaina",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.HIGH));
        initialCriteria.add(new ShopCriteria("Geras peržiūros / pardavimo santykis",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.PHOTOS), CriteriaWeight.HIGH));
        initialCriteria.add(new ShopCriteria("Produkto aprašymo kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        initialCriteria.add(new ShopCriteria("Kokybiški „Search terms",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));

        initialCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas „Search terms“",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Produkto pavadinime esantys nereikalingi žodžiai",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Pavadinimo kokybė",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Pardavimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Klientų atsiliepimų skaičius",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Klientų įvertinimai",
                Arrays.asList(Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Tinkamai parinktas nuotraukų dydis",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Produkto skelbimo užbaigtumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Produkto ypatybių išrašymas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Greitas apdorojimo laikas",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Geras POP (Perfect order percentage)",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Produktų defektų nusiskundimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Produktų inventoriaus sekimas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SHIPPING), CriteriaWeight.MEDIUM));
        initialCriteria.add(new ShopCriteria("Gera turinio kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));

        initialCriteria.add(new ShopCriteria("Atsakyti klausimai",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        initialCriteria.add(new ShopCriteria("Praleistas laikas produkto puslapyje",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        initialCriteria.add(new ShopCriteria("Produkto aprašymo raktažodžiai",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        initialCriteria.add(new ShopCriteria("Produkto specifikacijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        initialCriteria.add(new ShopCriteria("Kategorijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        initialCriteria.add(new ShopCriteria("Žemas Exit rate",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));

        return initialCriteria;
    }

    public List<ShopCriteria> getAllPossibleValues() {
        List<ShopCriteria> allCriteria = new ArrayList<>();

        allCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Optimali produkto kaina",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Geras peržiūros / pardavimo santykis",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.PHOTOS), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Produkto aprašymo kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Kokybiški „Search terms",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));

        allCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas „Search terms“",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Produkto pavadinime esantys nereikalingi žodžiai",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Pavadinimo kokybė",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Pardavimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Klientų atsiliepimų skaičius",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Klientų įvertinimai",
                Arrays.asList(Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Tinkamai parinktas nuotraukų dydis",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Produkto skelbimo užbaigtumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Produkto ypatybių išrašymas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Greitas apdorojimo laikas",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Geras POP (Perfect order percentage)",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Produktų defektų nusiskundimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Produktų inventoriaus sekimas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SHIPPING), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Gera turinio kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));

        allCriteria.add(new ShopCriteria("Atsakyti klausimai",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Praleistas laikas produkto puslapyje",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto aprašymo raktažodžiai",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto specifikacijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Kategorijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Žemas Exit rate",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));

        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Užsakymų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Atsiliepimų skaičius",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Atsiliepimų bendras įvertis",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Specifiškai AliExpress atrinkti produktai AliExpress Choice",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PRICE, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY, Category.PHOTOS, Category.SHIPPING, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));

        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Vartotojo termino dalies buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Aukštos kokybės nuotraukos",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Pardavėjo atsakymo dažnis",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Konkurencinga produkto kaina",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Produkto turinio kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Produkto aprašymo kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Produkto kategorijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));

        allCriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Nemokamas siuntimas",
                Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Siuntimas su grąžinimu",
                Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Paspaudimų skaičius",
                Arrays.asList(Category.PHOTOS), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Įsiminimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Užpildytos specifikacijos",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto puslapio greitis",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto puslapio pritaikymas įvairiems įrenginiams",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto puslapyje esančios ALT žymos",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto ALT žymose esantys raktažodžiai",
                Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Meta žymos",
                Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Raktažodžiai Meta žymose",
                Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));

        allCriteria.add(new ShopCriteria("Vartotojo termino tikslus atitikimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Produkto aprašymo kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));

        allCriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Produkto pavadinimo kokybė",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Aukštos kokybės nuotraukos",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Kategorijų pridėjimas",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Pardavėjo klientų aptarnavimo įvertis",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Pardavėjo atsakymo laikas",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Pardavėjo atsiliepimai",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));

        allCriteria.add(new ShopCriteria("Produkto aprašymo baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Tinkamai panaudotos žodžių kombinacijos",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Papildomos produkto nuotraukos",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto vaizdo įrašai",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Alternatyvios HTML žymos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Tinkamai parinktas šrifto dydis",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Raktažodžiai alternatyviosiose HTML žymose",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Tinkamai parinktos aprašymo spalvos",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Pardavėjo parduotuvės taisyklių baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Pardavėjo grąžinimo taisyklių baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto kaina",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto skelbimo baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto paspaudimai",
                Arrays.asList(Category.PHOTOS), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto įsiminimai",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto pardavimai",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.LOW));

        allCriteria.add(new ShopCriteria("Vartotojo termino tikslus atitikimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas žymose",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas žymose",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        allCriteria.add(new ShopCriteria("Vartotojo termino atitikimas produkto pavadinimo pradžioje",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));

        allCriteria.add(new ShopCriteria("Produkto paspaudimai",
                Arrays.asList(Category.PHOTOS), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Produkto įsiminimai",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Produkto pardavimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Pardavėjo klientų aptarnavimo įvertis",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        allCriteria.add(new ShopCriteria("Kainos korektiškumas",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));

        allCriteria.add(new ShopCriteria("Produkto atributai atitinkantys paieškos terminą",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Pardavėjo taisyklių laikymosi įvertis",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Pardavėjo „Apie” skilties baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Pardavėjo parduotuvės užpildytų polisių baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Pardavėjo parduotuvės pristatymo skilties baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Atsiliepimai",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto įkėlimo naujumas",
                Arrays.asList(), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto atnaujinimo naujumas",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto ir pirkėjo lokacija",
                Arrays.asList(), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto pavadinimo išvertimas į parduotuvės nurodytą kalbą",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Produkto žymų išvertimas į parduotuvės nurodytą kalbą",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Specifinės kategorijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Pagrindinės nuotraukos tikslumas",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        allCriteria.add(new ShopCriteria("Pardavėjo komunikavimas su buvusiais klientais",
                Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));

        return allCriteria;
    }
}
