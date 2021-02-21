package org.kodluyoruz.mybank.account.demanddepositaccount.concrete;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kodluyoruz.mybank.utilities.enums.currency.Currency;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class DemandDepositAccountControllerTest {
    RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    void getDemandDepositAccount() {
        DemandDepositAccount demandDepositAccount = restTemplate
                .getForObject("http://localhost:8080/api/v1/demand/8500831863100394", DemandDepositAccount.class);
        assertNotNull(demandDepositAccount);
    }

    @Test
    @DisplayName("Is money currency is EUR?")
    void getMoneyCurrency() {
        DemandDepositAccount demandDepositAccount = restTemplate
                .getForObject("http://localhost:8080/api/v1/demand/8500831863100394", DemandDepositAccount.class);
        assert demandDepositAccount != null;
        assertEquals(Currency.EUR, demandDepositAccount.getDemandDepositAccountCurrency());
    }
}