package org.kodluyoruz.mybank.account.savingsaccount.concrete;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountControllerTest {
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    @DisplayName("SavingsAccount info will get.")
    void get() {
        SavingsAccount savingsAccount = restTemplate
                .getForObject("http://localhost:8080/api/savings/8500010173201752", SavingsAccount.class);
        assertNotNull(savingsAccount);
    }

    @Test
    void savingsAccountCustomerName() {
        SavingsAccount savingsAccount = restTemplate
                .getForObject("http://localhost:8080/api/savings/8500010173201752", SavingsAccount.class);
        assertEquals(10000,savingsAccount.getSavingsAccountBalance());
    }
}