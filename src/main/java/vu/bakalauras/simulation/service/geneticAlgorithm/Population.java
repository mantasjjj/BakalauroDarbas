package vu.bakalauras.simulation.service.geneticAlgorithm;

import vu.bakalauras.simulation.Category;
import vu.bakalauras.simulation.model.customer.CriteriaImportance;
import vu.bakalauras.simulation.model.customer.Customer;
import vu.bakalauras.simulation.model.shop.CriteriaWeight;
import vu.bakalauras.simulation.model.shop.Shop;
import vu.bakalauras.simulation.model.shop.ShopCriteria;
import vu.bakalauras.simulation.service.CustomerGenerator;
import vu.bakalauras.simulation.service.SimulationService;

import java.util.*;
import java.util.stream.Collectors;

import static vu.bakalauras.simulation.Category.SELLER_QUALITY;

public class Population {
    private static final Random RANDOM = new Random();
    SimulationService simulationService = new SimulationService();
    List<Shop> individuals = new ArrayList<>();
    List<Customer> customers = new ArrayList<>();
    double fittest = 0;

    public void initializeInitialPopulation() {
        List<Shop> initialShops = new ArrayList<>();

//        fillShopsWithSpecifiedWeight(initialShops, CriteriaWeight.HIGH);
//        fillShopsWithSpecifiedWeight(initialShops, CriteriaWeight.MEDIUM);
//        fillShopsWithSpecifiedWeight(initialShops, CriteriaWeight.LOW);

        fillIndividualsWithExistingData(initialShops);

//        for (int i = 0; i < 1000; i++) {
//            initialShops.add(fillNewShop());
//        }

        individuals = initialShops;
    }

    public void initializeCustomers() {
        CustomerGenerator customerGenerator = new CustomerGenerator();
        long seed = 210320085933565L;
        customers = customerGenerator.generateCustomerList(100, seed);
    }

    public Shop getFittest() {
        Shop answer = individuals.stream().sorted(Comparator.comparing(Shop::getScore).reversed()).collect(Collectors.toList()).get(0);
        fittest = answer.score;
        return answer;
    }

    public Shop getSecondFittest() {
        int secondIndex = 1;
        List<Shop> sortedShops = individuals.stream().sorted(Comparator.comparing(Shop::getScore).reversed()).collect(Collectors.toList());
        if (sortedShops.get(1).shopCriteria.stream().filter(s -> s.criteriaWeight != null).count() > 1) {
            return sortedShops.get(secondIndex);
        }
        List<ShopCriteria> shopCriteria = sortedShops.get(secondIndex).shopCriteria.stream().filter(s -> s.criteriaWeight != null).collect(Collectors.toList());
        List<ShopCriteria> fittestCriteria = sortedShops.get(0).shopCriteria.stream().filter(s -> s.criteriaWeight != null).collect(Collectors.toList());

        while (fittestCriteria.size() > 0 && shopCriteria.size() > 0 &&
                shopCriteria.get(0).name.equals(fittestCriteria.get(0).name)) {
            secondIndex++;
            shopCriteria = sortedShops.get(secondIndex).shopCriteria.stream().filter(s -> s.criteriaWeight != null).collect(Collectors.toList());
        }

        return sortedShops.get(secondIndex);
    }

    public int getLeastFittestIndex() {
        double minFitVal = Double.MAX_VALUE;
        int minFitIndex = 0;
        for (int i = 0; i < individuals.size(); i++) {
            if (minFitVal >= individuals.get(i).score) {
                minFitVal = individuals.get(i).score;
                minFitIndex = i;
            }
        }
        return minFitIndex;
    }

    public void calculateFitness() {
        for (int i = 0; i < individuals.size(); i++) {
            individuals.get(i).score = getTotalScore(customers, individuals.get(i), simulationService);
        }
        individuals = individuals.stream().sorted(Comparator.comparing(Shop::getScore).reversed()).collect(Collectors.toList());
        getFittest();
    }

    private void fillShopsWithSpecifiedWeight(List<Shop> initialShops, CriteriaWeight criteriaWeight) {
        for (int i = 0; i < getAllPossibleValues().size(); i++) {
            Shop shop = new Shop(getAllPossibleValues());
            shop.shopCriteria.get(i).criteriaWeight = criteriaWeight;
            initialShops.add(shop);
        }
    }

    private void fillIndividualsWithExistingData(List<Shop> initialShops) {
        initialShops.add(new Shop(fillAmazonShopCriteria()));
        initialShops.add(new Shop(fillAliExpressShopCriteria()));
        initialShops.add(new Shop(fillEtsyShopCriteria()));
        initialShops.add(new Shop(fillEBayShopCriteria()));
    }

    public Shop fillNewShop() {
        Random rn = new Random();
        rn.setSeed(210320085933565L);
        List<CriteriaWeight> criteriaWeights = Arrays.asList(CriteriaWeight.HIGH, CriteriaWeight.MEDIUM, CriteriaWeight.LOW);

        List<ShopCriteria> randomlyFilledCriteria = new ArrayList<>();
        List<ShopCriteria> allCriteria = getAllPossibleValues();
        while (randomlyFilledCriteria.size() < 30) {
            int randomWeight = rn.nextInt(criteriaWeights.size());

            ShopCriteria randomCriteria = allCriteria.get(RANDOM.nextInt(allCriteria.size()));
            randomCriteria.criteriaWeight = criteriaWeights.get(randomWeight);
            if (randomlyFilledCriteria.stream().filter(s -> s.name.equals(randomCriteria.name)).findFirst().isEmpty()) {
                randomlyFilledCriteria.add(randomCriteria);
            }
        }
        return new Shop(randomlyFilledCriteria);
    }

    public static double getTotalScore(List<Customer> customers, Shop shop, SimulationService simulationService) {
        double totalScore = 0;

        for (Customer customer : customers) {
            totalScore += simulationService.getShopScore(shop.shopCriteria, customer.customerCriteria, CriteriaImportance.MANDATORY, false);
        }

        long emptyCriteriaCount = shop.shopCriteria.stream().filter(s -> s.criteriaWeight == null).count();
        if (emptyCriteriaCount >= 77) {
            totalScore -= emptyCriteriaCount * 10;
        }

        return totalScore;
    }

    public List<ShopCriteria> getAllPossibleValues() {
        List<ShopCriteria> allCriteria = new ArrayList<>();

        allCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Optimali produkto kaina",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Geras peržiūros / pardavimo santykis",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.PHOTOS), null));
        allCriteria.add(new ShopCriteria("Produkto aprašymo kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Kokybiški „Search terms",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), null));

        allCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas „Search terms“",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Produkto pavadinime esantys nereikalingi žodžiai",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pavadinimo kokybė",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Klientų atsiliepimų skaičius",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS), null));
        allCriteria.add(new ShopCriteria("Klientų įvertinimai",
                Arrays.asList(Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Tinkamai parinktas nuotraukų dydis",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto skelbimo užbaigtumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto ypatybių išrašymas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Greitas apdorojimo laikas",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Geras POP (Perfect order percentage)",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produktų defektų nusiskundimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produktų inventoriaus sekimas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SHIPPING), null));
        allCriteria.add(new ShopCriteria("Gera turinio kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), null));

        allCriteria.add(new ShopCriteria("Atsakyti klausimai",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Praleistas laikas produkto puslapyje",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto aprašymo raktažodžiai",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto specifikacijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Kategorijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Žemas Exit rate",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY), null));

        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Užsakymų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Atsiliepimų skaičius",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS), null));
        allCriteria.add(new ShopCriteria("Atsiliepimų bendras įvertis",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PRODUCT_REVIEWS), null));

        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Vartotojo termino dalies buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Aukštos kokybės nuotraukos",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo atsakymo dažnis",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Konkurencinga produkto kaina",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto turinio kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto aprašymo kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto kategorijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), null));

        allCriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Nemokamas siuntimas",
                Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), null));
        allCriteria.add(new ShopCriteria("Siuntimas su grąžinimu",
                Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), null));
        allCriteria.add(new ShopCriteria("Paspaudimų skaičius",
                Arrays.asList(Category.PHOTOS), null));
        allCriteria.add(new ShopCriteria("Įsiminimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS, Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Užpildytos specifikacijos",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto puslapio greitis",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto puslapio pritaikymas įvairiems įrenginiams",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto puslapyje esančios ALT žymos",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Produkto ALT žymose esantys raktažodžiai",
                Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Meta žymos",
                Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Raktažodžiai Meta žymose",
                Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), null));

        allCriteria.add(new ShopCriteria("Vartotojo termino tikslus atitikimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Produkto aprašymo kokybė",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), null));

        allCriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Produkto pavadinimo kokybė",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Aukštos kokybės nuotraukos",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Kategorijų pridėjimas",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo klientų aptarnavimo įvertis",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo atsakymo laikas",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo atsiliepimai",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), null));

        allCriteria.add(new ShopCriteria("Produkto aprašymo baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Tinkamai panaudotos žodžių kombinacijos",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Papildomos produkto nuotraukos",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto vaizdo įrašai",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Alternatyvios HTML žymos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Tinkamai parinktas šrifto dydis",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Raktažodžiai alternatyviosiose HTML žymose",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Tinkamai parinktos aprašymo spalvos",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo parduotuvės taisyklių baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo grąžinimo taisyklių baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), null));
        allCriteria.add(new ShopCriteria("Produkto kaina",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto skelbimo baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto paspaudimai",
                Arrays.asList(Category.PHOTOS), null));
        allCriteria.add(new ShopCriteria("Produkto įsiminimai",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS), null));
        allCriteria.add(new ShopCriteria("Produkto pardavimai",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), null));

        allCriteria.add(new ShopCriteria("Vartotojo termino tikslus atitikimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas žymose",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Vartotojo termino buvimas žymose",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));
        allCriteria.add(new ShopCriteria("Vartotojo termino atitikimas produkto pavadinimo pradžioje",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), null));

        allCriteria.add(new ShopCriteria("Produkto paspaudimai",
                Arrays.asList(Category.PHOTOS), null));
        allCriteria.add(new ShopCriteria("Produkto įsiminimai",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS), null));
        allCriteria.add(new ShopCriteria("Produkto pardavimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo klientų aptarnavimo įvertis",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Kainos korektiškumas",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), null));

        allCriteria.add(new ShopCriteria("Produkto atributai atitinkantys paieškos terminą",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo taisyklių laikymosi įvertis",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo „Apie” skilties baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo parduotuvės užpildytų polisių baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo parduotuvės pristatymo skilties baigtinumas",
                Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), null));
        allCriteria.add(new ShopCriteria("Atsiliepimai",
                Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto įkėlimo naujumas",
                Arrays.asList(), null));
        allCriteria.add(new ShopCriteria("Produkto atnaujinimo naujumas",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto ir pirkėjo lokacija",
                Arrays.asList(), null));
        allCriteria.add(new ShopCriteria("Produkto pavadinimo išvertimas į parduotuvės nurodytą kalbą",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Produkto žymų išvertimas į parduotuvės nurodytą kalbą",
                Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Specifinės kategorijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), null));
        allCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pagrindinės nuotraukos tikslumas",
                Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), null));
        allCriteria.add(new ShopCriteria("Pardavėjo komunikavimas su buvusiais klientais",
                Arrays.asList(Category.SELLER_QUALITY), null));
        allCriteria.add(new ShopCriteria("Specifiškai AliExpress atrinkti produktai AliExpress Choice",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PRICE, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY, Category.PHOTOS, Category.SHIPPING, Category.PRODUCT_REVIEWS), null));


        return allCriteria;
    }

    private List<ShopCriteria> fillAmazonShopCriteria() {
        List<ShopCriteria> amazonCriteria = new ArrayList<>();

        amazonCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGHEST));

        amazonCriteria.add(new ShopCriteria("Produkto reklama",
                Arrays.asList(), CriteriaWeight.AD));

        amazonCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        amazonCriteria.add(new ShopCriteria("Optimali produkto kaina",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.HIGH));
        amazonCriteria.add(new ShopCriteria("Geras peržiūros / pardavimo santykis",
                Arrays.asList(SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.PHOTOS), CriteriaWeight.HIGH));
        amazonCriteria.add(new ShopCriteria("Produkto aprašymo kokybė",
                Arrays.asList(SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        amazonCriteria.add(new ShopCriteria("Kokybiški „Search terms",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));

        amazonCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas „Search terms“",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Produkto pavadinime esantys nereikalingi žodžiai",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, SELLER_QUALITY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Pavadinimo kokybė",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, SELLER_QUALITY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Pardavimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, SELLER_QUALITY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Klientų atsiliepimų skaičius",
                Arrays.asList(SELLER_QUALITY, Category.PRODUCT_REVIEWS), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Klientų įvertinimai",
                Arrays.asList(Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Tinkamai parinktas nuotraukų dydis",
                Arrays.asList(Category.PHOTOS, SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė",
                Arrays.asList(Category.PHOTOS, SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Produkto skelbimo užbaigtumas",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Produkto ypatybių išrašymas",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Greitas apdorojimo laikas",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Geras POP (Perfect order percentage)",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Produktų defektų nusiskundimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Produktų inventoriaus sekimas",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SHIPPING), CriteriaWeight.MEDIUM));
        amazonCriteria.add(new ShopCriteria("Gera turinio kokybė",
                Arrays.asList(SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));

        amazonCriteria.add(new ShopCriteria("Atsakyti klausimai",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.LOW));
        amazonCriteria.add(new ShopCriteria("Praleistas laikas produkto puslapyje",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        amazonCriteria.add(new ShopCriteria("Produkto aprašymo raktažodžiai",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        amazonCriteria.add(new ShopCriteria("Produkto specifikacijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        amazonCriteria.add(new ShopCriteria("Kategorijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        amazonCriteria.add(new ShopCriteria("Žemas Exit rate",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));

        return amazonCriteria;
    }

    private List<ShopCriteria> fillAliExpressShopCriteria() {
        List<ShopCriteria> aliExpressCriteria = new ArrayList<>();
        aliExpressCriteria.add(new ShopCriteria("Vartotojo termino dalies buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGHEST));

        aliExpressCriteria.add(new ShopCriteria("Produkto reklama",
                Arrays.asList(), CriteriaWeight.AD));

        aliExpressCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        aliExpressCriteria.add(new ShopCriteria("Užsakymų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, SELLER_QUALITY), CriteriaWeight.HIGH));
        aliExpressCriteria.add(new ShopCriteria("Atsiliepimų skaičius",
                Arrays.asList(SELLER_QUALITY, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));
        aliExpressCriteria.add(new ShopCriteria("Atsiliepimų bendras įvertis",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));
        aliExpressCriteria.add(new ShopCriteria("Specifiškai AliExpress atrinkti produktai AliExpress Choice",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PRICE, Category.INFORMATION_QUALITY, SELLER_QUALITY, Category.PHOTOS, Category.SHIPPING, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));

        aliExpressCriteria.add(new ShopCriteria("Vartotojo termino buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        aliExpressCriteria.add(new ShopCriteria("Vartotojo termino dalies buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        aliExpressCriteria.add(new ShopCriteria("Aukštos kokybės nuotraukos",
                Arrays.asList(Category.PHOTOS, SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        aliExpressCriteria.add(new ShopCriteria("Pardavėjo atsakymo dažnis",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.MEDIUM));
        aliExpressCriteria.add(new ShopCriteria("Konkurencinga produkto kaina",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        aliExpressCriteria.add(new ShopCriteria("Produkto turinio kokybė",
                Arrays.asList(SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        aliExpressCriteria.add(new ShopCriteria("Produkto aprašymo kokybė",
                Arrays.asList(SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        aliExpressCriteria.add(new ShopCriteria("Produkto kategorijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));

        aliExpressCriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        aliExpressCriteria.add(new ShopCriteria("Nemokamas siuntimas",
                Arrays.asList(SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        aliExpressCriteria.add(new ShopCriteria("Siuntimas su grąžinimu",
                Arrays.asList(SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        aliExpressCriteria.add(new ShopCriteria("Paspaudimų skaičius",
                Arrays.asList(Category.PHOTOS), CriteriaWeight.LOW));
        aliExpressCriteria.add(new ShopCriteria("Įsiminimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        aliExpressCriteria.add(new ShopCriteria("Užpildytos specifikacijos",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        aliExpressCriteria.add(new ShopCriteria("Produkto puslapio greitis",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.LOW));
        aliExpressCriteria.add(new ShopCriteria("Produkto puslapio pritaikymas įvairiems įrenginiams",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.LOW));
        aliExpressCriteria.add(new ShopCriteria("Produkto puslapyje esančios ALT žymos",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        aliExpressCriteria.add(new ShopCriteria("Produkto ALT žymose esantys raktažodžiai",
                Arrays.asList(SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        aliExpressCriteria.add(new ShopCriteria("Meta žymos",
                Arrays.asList(SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        aliExpressCriteria.add(new ShopCriteria("Raktažodžiai Meta žymose",
                Arrays.asList(SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));

        return aliExpressCriteria;
    }

    private List<ShopCriteria> fillEBayShopCriteria() {
        List<ShopCriteria> ebayriteria = new ArrayList<>();
        ebayriteria.add(new ShopCriteria("Vartotojo termino ar sinonimų buvimas pavadinime ar aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGHEST));

        ebayriteria.add(new ShopCriteria("Produkto reklama",
                Arrays.asList(), CriteriaWeight.AD));

        ebayriteria.add(new ShopCriteria("Vartotojo termino tikslus atitikimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        ebayriteria.add(new ShopCriteria("Vartotojo termino buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        ebayriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        ebayriteria.add(new ShopCriteria("Produkto aprašymo kokybė",
                Arrays.asList(SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));

        ebayriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        ebayriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas aprašyme",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        ebayriteria.add(new ShopCriteria("Produkto pavadinimo kokybė",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        ebayriteria.add(new ShopCriteria("Aukštos kokybės nuotraukos",
                Arrays.asList(Category.PHOTOS, SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        ebayriteria.add(new ShopCriteria("Kategorijų pridėjimas",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        ebayriteria.add(new ShopCriteria("Pardavėjo klientų aptarnavimo įvertis",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.MEDIUM));
        ebayriteria.add(new ShopCriteria("Pardavėjo atsakymo laikas",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.MEDIUM));
        ebayriteria.add(new ShopCriteria("Pardavėjo atsiliepimai",
                Arrays.asList(SELLER_QUALITY, Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));

        ebayriteria.add(new ShopCriteria("Produkto aprašymo baigtinumas",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Tinkamai panaudotos žodžių kombinacijos",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Papildomos produkto nuotraukos",
                Arrays.asList(Category.PHOTOS, SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Produkto vaizdo įrašai",
                Arrays.asList(Category.PHOTOS, SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Alternatyvios HTML žymos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Tinkamai parinktas šrifto dydis",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Raktažodžiai alternatyviosiose HTML žymose",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Tinkamai parinktos aprašymo spalvos",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Pardavėjo parduotuvės taisyklių baigtinumas",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Pardavėjo grąžinimo taisyklių baigtinumas",
                Arrays.asList(SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Produkto kaina",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Produkto skelbimo baigtinumas",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Produkto paspaudimai",
                Arrays.asList(Category.PHOTOS), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Produkto įsiminimai",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS), CriteriaWeight.LOW));
        ebayriteria.add(new ShopCriteria("Produkto pardavimai",
                Arrays.asList(Category.PRODUCT_QUALITY, SELLER_QUALITY), CriteriaWeight.LOW));

        return ebayriteria;
    }

    private List<ShopCriteria> fillEtsyShopCriteria() {
        List<ShopCriteria> etsyCriteria = new ArrayList<>();
        etsyCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime arba žymose",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGHEST));

        etsyCriteria.add(new ShopCriteria("Produkto reklama",
                Arrays.asList(), CriteriaWeight.AD));

        etsyCriteria.add(new ShopCriteria("Vartotojo termino tikslus atitikimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        etsyCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        etsyCriteria.add(new ShopCriteria("Vartotojo termino buvimas žymose",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        etsyCriteria.add(new ShopCriteria("Vartotojo termino buvimas žymose",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        etsyCriteria.add(new ShopCriteria("Vartotojo termino atitikimas produkto pavadinimo pradžioje",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));

        etsyCriteria.add(new ShopCriteria("Produkto paspaudimai",
                Arrays.asList(Category.PHOTOS), CriteriaWeight.MEDIUM));
        etsyCriteria.add(new ShopCriteria("Produkto įsiminimai",
                Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS), CriteriaWeight.MEDIUM));
        etsyCriteria.add(new ShopCriteria("Produkto pardavimų skaičius",
                Arrays.asList(Category.PRODUCT_QUALITY, SELLER_QUALITY), CriteriaWeight.MEDIUM));
        etsyCriteria.add(new ShopCriteria("Pardavėjo klientų aptarnavimo įvertis",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.MEDIUM));
        etsyCriteria.add(new ShopCriteria("Kainos korektiškumas",
                Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));

        etsyCriteria.add(new ShopCriteria("Produkto atributai atitinkantys paieškos terminą",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Pardavėjo taisyklių laikymosi įvertis",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Pardavėjo „Apie” skilties baigtinumas",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Pardavėjo parduotuvės užpildytų polisių baigtinumas",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Pardavėjo parduotuvės pristatymo skilties baigtinumas",
                Arrays.asList(SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Atsiliepimai",
                Arrays.asList(SELLER_QUALITY, Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Produkto įkėlimo naujumas",
                Arrays.asList(), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Produkto atnaujinimo naujumas",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Produkto ir pirkėjo lokacija",
                Arrays.asList(), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Produkto pavadinimo išvertimas į parduotuvės nurodytą kalbą",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Produkto žymų išvertimas į parduotuvės nurodytą kalbą",
                Arrays.asList(SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Specifinės kategorijos",
                Arrays.asList(Category.SEARCH_RESULT_ACCURACY, SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė",
                Arrays.asList(Category.PHOTOS, SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Pagrindinės nuotraukos tikslumas",
                Arrays.asList(Category.PHOTOS, SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        etsyCriteria.add(new ShopCriteria("Pardavėjo komunikavimas su buvusiais klientais",
                Arrays.asList(SELLER_QUALITY), CriteriaWeight.LOW));

        return etsyCriteria;
    }

}
