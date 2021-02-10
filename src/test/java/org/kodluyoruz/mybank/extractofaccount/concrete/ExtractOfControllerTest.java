package org.kodluyoruz.mybank.extractofaccount.concrete;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class ExtractOfControllerTest {
    RestTemplate restTemplate;
    ExtractOfAccount extractOfAccount = new ExtractOfAccount();
    private String uri = "http://localhost:8080/api/extractofaccount";

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        extractOfAccount = restTemplate.getForObject(uri + "/1/extract", ExtractOfAccount.class);
    }

    @Test
    @DisplayName("ExtractOfAccount will get with extractID and term debt will control.")
    void get() {
        assert extractOfAccount != null;
        assertTrue(extractOfAccount.getTermDebt() > 0);
    }

    @Test
    @DisplayName("Shopping Interest Rate equal to  1.79")
    void shoppingInterestRateEqualToOneDoySeventyNine() {
        extractOfAccount = restTemplate.getForObject(uri + "/1/extract", ExtractOfAccount.class);
        assert extractOfAccount != null;
        assertEquals(1.79, extractOfAccount.getShoppingInterestRate());
    }
}