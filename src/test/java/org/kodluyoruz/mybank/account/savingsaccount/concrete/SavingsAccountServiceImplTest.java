package org.kodluyoruz.mybank.account.savingsaccount.concrete;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kodluyoruz.mybank.account.savingsaccount.abtrct.SavingsAccountRepository;
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
class SavingsAccountServiceImplTest {
    @Mock
    SavingsAccountRepository savingsAccountRepository;

    @InjectMocks
    SavingsAccountServiceImpl savingsAccountService;

    static SavingsAccount savingsAccount = new SavingsAccount();

    @BeforeAll
    static void init() {
        savingsAccount.setSavingsAccountNumber(Long.parseLong(Account.generateAccount.get()));
        savingsAccount.setSavingsAccountIBAN(Iban.generateIban.apply(String.valueOf(savingsAccount.getSavingsAccountNumber())));
        savingsAccount.setSavingsAccountBalance(15000);
        savingsAccount.setSavingsAccountCurrency(Currency.EUR);
        savingsAccount.setSavingsAccountCreationDate(LocalDate.of(2020, 5, 6));
        savingsAccount.setSavingsAccountInterestRate(2.45);
        savingsAccount.setCustomer(null);
        savingsAccount.setBankCard(null);
    }

    @BeforeEach
    void setMockOutput() {
        savingsAccountService = new SavingsAccountServiceImpl(savingsAccountRepository, null, null, null, null);
        savingsAccountRepository.save(savingsAccount);
    }

    @Test
    void getByAccountIban() {
        Mockito.when(savingsAccountRepository.findSavingsAccountBySavingsAccountIBAN(savingsAccount.getSavingsAccountIBAN())).thenReturn(savingsAccount);
        assertEquals(Currency.EUR, savingsAccountService
                .getByAccountIban(savingsAccount.getSavingsAccountIBAN())
                .getSavingsAccountCurrency());
    }
    @Test
    void theBalanceLowerThanTwentyThousand(){
        Mockito.when(savingsAccountRepository.findSavingsAccountBySavingsAccountIBAN(savingsAccount.getSavingsAccountIBAN())).thenReturn(savingsAccount);
        assertTrue(savingsAccountService
                .getByAccountIban(savingsAccount.getSavingsAccountIBAN())
                .getSavingsAccountBalance()<20000);
    }
}