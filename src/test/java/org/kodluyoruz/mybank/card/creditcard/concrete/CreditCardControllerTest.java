package org.kodluyoruz.mybank.card.creditcard.concrete;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class CreditCardControllerTest {
    RestTemplate restTemplate;
    CreditCard creditCard;
    @BeforeEach
    void setUp(){
        restTemplate = new RestTemplate();
        creditCard = restTemplate
                .getForObject("http://localhost:8080/api/card/8500768641657976",CreditCard.class);
    }
    @Test
    void get() {
        assert creditCard != null;
        Assertions.assertEquals("Isa SAYAR",creditCard.getCardNameSurname());
    }
    @Test
    void debtIsZero(){
        assert creditCard != null;
        Assertions.assertEquals(0,creditCard.getCardDebt());
    }

}