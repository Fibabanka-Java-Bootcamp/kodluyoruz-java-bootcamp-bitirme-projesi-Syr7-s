package org.kodluyoruz.mybank.account.demanddepositaccount.concrete;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kodluyoruz.mybank.account.demanddepositaccount.abstrct.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.utilities.enums.currency.Currency;
import org.kodluyoruz.mybank.utilities.generate.accountgenerate.Account;
import org.kodluyoruz.mybank.utilities.generate.ibangenerate.Iban;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DemandDepositAccountServiceImplTest {
    @Mock
    DemandDepositAccountRepository demandDepositAccountRepository;

    @InjectMocks
    DemandDepositAccountServiceImpl demandDepositAccountService;

    static DemandDepositAccount demandDepositAccount = new DemandDepositAccount();

    @BeforeAll
    static void init() {
        demandDepositAccount.setDemandDepositAccountNumber(Long.parseLong(Account.generateAccount.get()));
        demandDepositAccount.setDemandDepositAccountIBAN(Iban.generateIban.apply(String.valueOf(demandDepositAccount.getDemandDepositAccountNumber())));
        demandDepositAccount.setDemandDepositAccountBalance(15000);
        demandDepositAccount.setDemandDepositAccountCurrency(Currency.TRY);
        demandDepositAccount.setDemandDepositAccountCreationDate(LocalDate.of(2020, 12, 30));
        demandDepositAccount.setCustomer(null);
        demandDepositAccount.setBankCard(null);
    }

    @BeforeEach
    void setMockOutput() {
        demandDepositAccountService = new DemandDepositAccountServiceImpl(demandDepositAccountRepository, null, null, null, null, null);
        demandDepositAccountRepository.save(demandDepositAccount);
    }

    @Test
    void getByAccountIban() {
        Mockito.when(demandDepositAccountRepository.findDemandDepositAccountByDemandDepositAccountIBAN(demandDepositAccount.getDemandDepositAccountIBAN())).thenReturn(demandDepositAccount);
        assertEquals(Currency.TRY, demandDepositAccount.getDemandDepositAccountCurrency());
    }

    @Test
    void theBalanceGreaterThan10000() {
        Mockito.when(demandDepositAccountRepository.findDemandDepositAccountByDemandDepositAccountIBAN(demandDepositAccount.getDemandDepositAccountIBAN())).thenReturn(demandDepositAccount);
        assertTrue(demandDepositAccount.getDemandDepositAccountBalance() > 10000);
    }
}