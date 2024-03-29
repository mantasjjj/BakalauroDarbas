package vu.bakalauras.simulation.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vu.bakalauras.simulation.Category;
import vu.bakalauras.simulation.model.customer.Customer;
import vu.bakalauras.simulation.model.owner.Owner;
import vu.bakalauras.simulation.model.seller.Seller;
import vu.bakalauras.simulation.model.shop.CriteriaWeight;
import vu.bakalauras.simulation.model.shop.Shop;
import vu.bakalauras.simulation.model.shop.ShopCriteria;
import vu.bakalauras.simulation.service.CustomerGenerator;
import vu.bakalauras.simulation.service.CustomerService;
import vu.bakalauras.simulation.service.SellerRankingService;
import vu.bakalauras.simulation.service.ShopService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static vu.bakalauras.simulation.Category.SELLER_QUALITY;

@Component
public class AbstractCommandLineRunner implements CommandLineRunner {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private CustomerGenerator customerGenerator;

    @Autowired
    private SellerRankingService sellerRankingService;

    @Override
    public void run(String... args) {

        List<Customer> customers = fillCustomers();
        if (customers != null) {
            customers.forEach(c -> customerService.add(c));
        }

        List<Shop> shops = fillShops();
        if (shops != null) {
            shops.forEach(s -> shopService.add(s));
        }
    }

    public List<Customer> fillCustomers() {
        return customerGenerator.generateCustomerList();
    }

    public List<Shop> fillShops() {
        List<Shop> shops = new ArrayList<>();

        //Amazon
        List<ShopCriteria> amazonCriteria = fillAmazonShopCriteria();
        List<Seller> amazonSellers = fillInitialSellers();
        Owner amazonOwner = new Owner(365, 300);
        shops.add(new Shop("Shop 3", "KM3", amazonCriteria, fillRankedSellers(amazonCriteria, amazonSellers), BigDecimal.valueOf(141.74), amazonOwner));

        //EBay
        List<ShopCriteria> ebayCriteria = fillEBayShopCriteria();
        List<Seller> ebaySellers = fillInitialSellers();
        Owner ebayOwner = new Owner(149, 300);
        shops.add(new Shop("Shop 2", "KM2", ebayCriteria, fillRankedSellers(ebayCriteria, ebaySellers), BigDecimal.valueOf(140.8), ebayOwner));

        //AliExpress
        List<ShopCriteria> aliExpressCriteria = fillAliExpressShopCriteria();
        List<Seller> aliExpressSellers = fillInitialSellers();
        Owner aliExpressOwner = new Owner(251, 300);
        shops.add(new Shop("Shop 4", "KM4", aliExpressCriteria, fillRankedSellers(aliExpressCriteria, aliExpressSellers), BigDecimal.valueOf(200.74), aliExpressOwner));

        //Etsy
        List<ShopCriteria> etsyCriteria = fillEtsyShopCriteria();
        List<Seller> etsySellers = fillInitialSellers();
        Owner etsyOwner = new Owner(7, 300);
        shops.add(new Shop("Shop 1", "KM1", etsyCriteria, fillRankedSellers(etsyCriteria, etsySellers), BigDecimal.valueOf(30), etsyOwner));

        //New Shop
        List<ShopCriteria> newShopCriteria = fillGA3ShopCriteria();
        List<Seller> newShopSellers = fillInitialSellers();
        Owner newShopOwner = new Owner(7, 300);
        shops.add(new Shop("GA3 Shop", "GA3", newShopCriteria, fillRankedSellers(newShopCriteria, newShopSellers), BigDecimal.ZERO, newShopOwner));
//
//        List<ShopCriteria> GA1 = fillGA1ShopCriteria();
//        List<Seller> newShopSellers = fillInitialSellers();
//        Owner newShopOwner = new Owner(100, 300);
//        shops.add(new Shop("GA1 Shop", "GA1", GA1, fillRankedSellers(GA1, newShopSellers), BigDecimal.ZERO, newShopOwner));
//
//        List<ShopCriteria> GA2 = fillGA2ShopCriteria();
//        List<Seller> newShopSellers1 = fillInitialSellers();
//        Owner newShopOwner1 = new Owner(7, 300);
//        shops.add(new Shop("GA2 Shop", "GA2", GA2, fillRankedSellers(GA2, newShopSellers1), BigDecimal.ZERO, newShopOwner1));
//
//        List<ShopCriteria> GA3 = fillGA3ShopCriteria();
//        List<Seller> newShopSellers2 = fillInitialSellers();
//        Owner newShopOwner2 = new Owner(7, 300);
//        shops.add(new Shop("GA3 Shop", "GA3", GA3, fillRankedSellers(GA3, newShopSellers2), BigDecimal.ZERO, newShopOwner2));
//
//        List<ShopCriteria> GA4 = fillGA4ShopCriteria();
//        List<Seller> newShopSellers3 = fillInitialSellers();
//        Owner newShopOwner3 = new Owner(7, 300);
//        shops.add(new Shop("GA4 Shop", "GA4", GA4, fillRankedSellers(GA4, newShopSellers3), BigDecimal.ZERO, newShopOwner3));

        return shops;
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

    private List<Seller> fillRankedSellers(List<ShopCriteria> shopCriteria, List<Seller> sellers) {
        return sellerRankingService.getRankedSellers(shopCriteria, sellers);
    }

    private List<Seller> fillInitialSellers() {
        List<Seller> sellers = new ArrayList<>();

        sellers.add(new Seller(Arrays.asList(Category.PRICE, SELLER_QUALITY, Category.SHIPPING), false, false, 2));
        sellers.add(new Seller(Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY, Category.PRODUCT_REVIEWS), false, false, 2));
        sellers.add(new Seller(Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), false, false, 3));
        sellers.add(new Seller(Arrays.asList(SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), false, true, 2));
        sellers.add(new Seller(Arrays.asList(SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.PHOTOS), false, false, 3));
        sellers.add(new Seller(Arrays.asList(SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.PRODUCT_REVIEWS), false, true, 4));
        sellers.add(new Seller(Arrays.asList(SELLER_QUALITY, Category.SHIPPING, Category.PHOTOS), true, false, 3));
        sellers.add(new Seller(Arrays.asList(Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY, Category.SHIPPING), false, true, 5));
        sellers.add(new Seller(Arrays.asList(Category.PHOTOS, Category.PRODUCT_QUALITY, Category.PRICE), false, false, 4));
        sellers.add(new Seller(Arrays.asList(Category.INFORMATION_QUALITY, Category.PRICE, Category.SHIPPING), true, false, 2));
        sellers.add(new Seller(Arrays.asList(Category.PRODUCT_QUALITY, Category.PRODUCT_QUALITY, SELLER_QUALITY), true, true, 3));
        sellers.add(new Seller(Arrays.asList(Category.PRICE, Category.PRICE, SELLER_QUALITY), true, true, 4));
        sellers.add(new Seller(Arrays.asList(Category.INFORMATION_QUALITY, SELLER_QUALITY, Category.INFORMATION_QUALITY), false, false, 2));
        sellers.add(new Seller(Arrays.asList(Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY, Category.PRODUCT_QUALITY), false, false, 1));

        return sellers;
    }

    private List<ShopCriteria> fillGeneticAlgorithmCriteria() {
        List<ShopCriteria> shopCriteria = new ArrayList<>();

        shopCriteria.add(new ShopCriteria("Produkto atnaujinimo naujumas", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto ypatybių išrašymas", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produktų inventoriaus sekimas", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SHIPPING), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Specifiškai AliExpress atrinkti produktai AliExpress Choice", Arrays.asList(Category.PRODUCT_QUALITY, Category.PRICE, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY, Category.PHOTOS, Category.SHIPPING, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto įsiminimai", Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Kategorijos", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto paspaudimai", Arrays.asList(Category.PHOTOS), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto aprašymo kokybė", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto aprašymo raktažodžiai", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Tinkamai panaudotos žodžių kombinacijos", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Aukštos kokybės nuotraukos", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Atsiliepimų bendras įvertis", Arrays.asList(Category.PRODUCT_QUALITY, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Geras peržiūros / pardavimo santykis", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.PHOTOS), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto kaina", Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto kategorijos", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Pardavėjo parduotuvės taisyklių baigtinumas", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto žymų išvertimas į parduotuvės nurodytą kalbą", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto ir pirkėjo lokacija", Arrays.asList(), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Vartotojo termino buvimas žymose", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto įkėlimo naujumas", Arrays.asList(), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Geras POP (Perfect order percentage)", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Gera turinio kokybė", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Papildomos produkto nuotraukos", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Atsiliepimų skaičius", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Pardavėjo klientų aptarnavimo įvertis", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Tinkamai parinktas šrifto dydis", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Pardavėjo atsiliepimai", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Žemas Exit rate", Arrays.asList(Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));

        return shopCriteria;
    }

    public List<ShopCriteria> fillGA1ShopCriteria() {
        //Sukurta naudojant visus kriterijus be svoriu
        //Crossover - atsitiktinis
        List<ShopCriteria> shopCriteria = new ArrayList<>();

        shopCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas aprašyme", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Geras peržiūros / pardavimo santykis", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.PHOTOS), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto aprašymo kokybė", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto pavadinime esantys nereikalingi žodžiai", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Klientų atsiliepimų skaičius", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto specifikacijos", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Užsakymų skaičius", Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Vartotojo termino buvimas aprašyme", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Siuntimas su grąžinimu", Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Paspaudimų skaičius", Arrays.asList(Category.PHOTOS), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Įsiminimų skaičius", Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Užpildytos specifikacijos", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto puslapio pritaikymas įvairiems įrenginiams", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto ALT žymose esantys raktažodžiai", Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Vartotojo termino tikslus atitikimas pavadinime", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Vartotojo termino buvimas aprašyme", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto pavadinimo kokybė", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Pardavėjo klientų aptarnavimo įvertis", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Tinkamai panaudotos žodžių kombinacijos", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto vaizdo įrašai", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Tinkamai parinktos aprašymo spalvos", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Pardavėjo parduotuvės taisyklių baigtinumas", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Pardavėjo grąžinimo taisyklių baigtinumas", Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto kaina", Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto įsiminimai", Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Vartotojo termino buvimas žymose", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto pardavimų skaičius", Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Pardavėjo klientų aptarnavimo įvertis", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Pardavėjo taisyklių laikymosi įvertis", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto žymų išvertimas į parduotuvės nurodytą kalbą", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Specifinės kategorijos", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Specifiškai AliExpress atrinkti produktai AliExpress Choice", Arrays.asList(Category.PRODUCT_QUALITY, Category.PRICE, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY, Category.PHOTOS, Category.SHIPPING, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));

        return shopCriteria;
    }
    
    public List<ShopCriteria> fillGA2ShopCriteria() {
        //Sukurta naudojant visus kriterijus be svoriu
        //Crossover - 50%
        List<ShopCriteria> shopCriteria = new ArrayList<>();

        shopCriteria.add(new ShopCriteria("Geras peržiūros / pardavimo santykis", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.PHOTOS), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto aprašymo kokybė", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Vartotojo termino ar jo sinonimų buvimas „Search terms“", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto pavadinime esantys nereikalingi žodžiai", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Pardavimų skaičius", Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Tinkamai parinktas nuotraukų dydis", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto skelbimo užbaigtumas", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto ypatybių išrašymas", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto aprašymo raktažodžiai", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Užsakymų skaičius", Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto aprašymo kokybė", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto puslapio greitis", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto puslapio pritaikymas įvairiems įrenginiams", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Meta žymos", Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Raktažodžiai Meta žymose", Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Vartotojo termino buvimas aprašyme", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto aprašymo kokybė", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas aprašyme", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Pardavėjo klientų aptarnavimo įvertis", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto paspaudimai", Arrays.asList(Category.PHOTOS), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto įsiminimai", Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto pardavimų skaičius", Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Kainos korektiškumas", Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Pardavėjo taisyklių laikymosi įvertis", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Pardavėjo „Apie” skilties baigtinumas", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Pardavėjo parduotuvės pristatymo skilties baigtinumas", Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto įkėlimo naujumas", Arrays.asList(), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto žymų išvertimas į parduotuvės nurodytą kalbą", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Pagrindinės nuotraukos tikslumas", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Specifiškai AliExpress atrinkti produktai AliExpress Choice", Arrays.asList(Category.PRODUCT_QUALITY, Category.PRICE, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY, Category.PHOTOS, Category.SHIPPING, Category.PRODUCT_REVIEWS), CriteriaWeight.MEDIUM));

        return shopCriteria;
    }

    public List<ShopCriteria> fillGA3ShopCriteria() {
        //Sukurta kuriant modelius nuo nulio
        //Crossover - 75%
        List<ShopCriteria> shopCriteria = new ArrayList<>();

        shopCriteria.add(new ShopCriteria("Produkto pavadinime esantys nereikalingi žodžiai", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Pardavimų skaičius", Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto skelbimo užbaigtumas", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto ypatybių išrašymas", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produktų inventoriaus sekimas", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto aprašymo raktažodžiai", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Vartotojo termino dalies buvimas aprašyme", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Konkurencinga produkto kaina", Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto ALT žymose esantys raktažodžiai", Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Meta žymos", Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Vartotojo termino tikslus atitikimas pavadinime", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto pavadinimo kokybė", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Kategorijų pridėjimas", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Pardavėjo atsiliepimai", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Tinkamai parinktas šrifto dydis", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Tinkamai parinktos aprašymo spalvos", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto skelbimo baigtinumas", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto įsiminimai", Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto pardavimų skaičius", Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Pardavėjo taisyklių laikymosi įvertis", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Atsiliepimai", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto atnaujinimo naujumas", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Specifinės kategorijos", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Pardavėjo komunikavimas su buvusiais klientais", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Specifiškai AliExpress atrinkti produktai AliExpress Choice", Arrays.asList(Category.PRODUCT_QUALITY, Category.PRICE, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY, Category.PHOTOS, Category.SHIPPING, Category.PRODUCT_REVIEWS), CriteriaWeight.MEDIUM));

        return shopCriteria;
    }
    
    public List<ShopCriteria> fillGA4ShopCriteria() {
        //Sukurta naudojant visus kriterijus be svoriu
        //Crossover - 25%
        List<ShopCriteria> shopCriteria = new ArrayList<>();

        shopCriteria.add(new ShopCriteria("Produkto pavadinime esantys nereikalingi žodžiai", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Pardavimų skaičius", Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Geras POP (Perfect order percentage)", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto aprašymo raktažodžiai", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Kategorijos", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Atsiliepimų bendras įvertis", Arrays.asList(Category.PRODUCT_QUALITY, Category.PRODUCT_REVIEWS), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas aprašyme", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto puslapio pritaikymas įvairiems įrenginiams", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto ALT žymose esantys raktažodžiai", Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Meta žymos", Arrays.asList(Category.SELLER_QUALITY, Category.SEARCH_RESULT_ACCURACY, Category.INFORMATION_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Vartotojo termino sinonimų buvimas pavadinime", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto pavadinimo kokybė", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Aukštos kokybės nuotraukos", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Pardavėjo klientų aptarnavimo įvertis", Arrays.asList(Category.SELLER_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Pardavėjo atsiliepimai", Arrays.asList(Category.SELLER_QUALITY, Category.PRODUCT_REVIEWS, Category.PRODUCT_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto vaizdo įrašai", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Alternatyvios HTML žymos", Arrays.asList(Category.SEARCH_RESULT_ACCURACY, Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto kaina", Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Produkto įsiminimai", Arrays.asList(Category.PRODUCT_QUALITY, Category.PHOTOS), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Produkto pardavimai", Arrays.asList(Category.PRODUCT_QUALITY, Category.SELLER_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Vartotojo termino tikslus atitikimas pavadinime", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Vartotojo termino buvimas pavadinime", Arrays.asList(Category.SEARCH_RESULT_ACCURACY), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Kainos korektiškumas", Arrays.asList(Category.PRICE, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Pardavėjo parduotuvės pristatymo skilties baigtinumas", Arrays.asList(Category.SELLER_QUALITY, Category.SHIPPING), CriteriaWeight.LOW));
        shopCriteria.add(new ShopCriteria("Produkto žymų išvertimas į parduotuvės nurodytą kalbą", Arrays.asList(Category.SELLER_QUALITY, Category.INFORMATION_QUALITY), CriteriaWeight.HIGH));
        shopCriteria.add(new ShopCriteria("Aukšta nuotraukų kokybė", Arrays.asList(Category.PHOTOS, Category.SELLER_QUALITY, Category.PRODUCT_QUALITY), CriteriaWeight.MEDIUM));
        shopCriteria.add(new ShopCriteria("Specifiškai AliExpress atrinkti produktai AliExpress Choice", Arrays.asList(Category.PRODUCT_QUALITY, Category.PRICE, Category.INFORMATION_QUALITY, Category.SELLER_QUALITY, Category.PHOTOS, Category.SHIPPING, Category.PRODUCT_REVIEWS), CriteriaWeight.HIGH));

        return shopCriteria;
    }
    
}
