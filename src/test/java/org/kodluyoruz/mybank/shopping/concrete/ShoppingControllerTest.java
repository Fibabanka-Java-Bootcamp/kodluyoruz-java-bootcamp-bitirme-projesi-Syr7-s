package org.kodluyoruz.mybank.shopping.concrete;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kodluyoruz.mybank.utilities.enums.currency.Currency;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
class ShoppingControllerTest {
    RestTemplate restTemplate = new RestTemplate();
    Shopping shopping = new Shopping();

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    void doShopping() {
        shopping.setProductType("Furniture");
        shopping.setProductName("Carpet");
        shopping.setProductPrice(1000);
        shopping.setCurrency(Currency.TRY);
        shopping.setProductReceiveDate(LocalDate.of(2021, 2, 2));
        URI location = restTemplate.postForLocation("http://localhost:8080/api/shopping/8500495434241604?password=1996", shopping);
        assert location != null;
        Shopping editedShopping = restTemplate.getForObject(location,Shopping.class);
        assert editedShopping !=null;
        Assertions.assertEquals("Carpet",editedShopping.getProductName());
    }
}
