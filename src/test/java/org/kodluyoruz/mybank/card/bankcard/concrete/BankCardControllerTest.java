package org.kodluyoruz.mybank.card.bankcard.concrete;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;


import static org.junit.jupiter.api.Assertions.*;

class BankCardControllerTest {
    RestTemplate restTemplate;

    @BeforeEach
    void setUP() {
        restTemplate = new RestTemplate();
    }

    @Test
    void getBankCard() {
        BankCard bankCard = restTemplate
                .getForObject("http://localhost:8080/api/bankcard/8500472435626214/card",BankCard.class);
        assert bankCard != null;
        Assertions.assertEquals("Isa SAYAR",bankCard.getBankCardNameSurname());
    }
}