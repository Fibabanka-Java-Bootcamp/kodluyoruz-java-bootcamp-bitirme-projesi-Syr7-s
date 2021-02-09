package org.kodluyoruz.mybank.card.creditcard.concrete;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardRepository;
import org.kodluyoruz.mybank.utilities.enums.currency.Currency;
import org.kodluyoruz.mybank.utilities.generate.accountgenerate.Account;
import org.kodluyoruz.mybank.utilities.generate.securitycodegenerate.SecurityCodeGenerate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreditCardServiceImplTest {
    @Mock
    CreditCardRepository creditCardRepository;

    @InjectMocks
    CreditCardServiceImpl creditCardService;

    static CreditCard creditCard = new CreditCard();

    @BeforeAll
    static void firstSetUp() {
        creditCard.setCardAccountNumber(Long.parseLong(Account.generateAccount.get()));
        creditCard.setCardNameSurname("Isa SAYAR");
        creditCard.setCardPassword(1996);
        creditCard.setExpirationDate(LocalDate.of(2020, 2, 1));
        creditCard.setSecurityCode(SecurityCodeGenerate.securityCode.get());
        creditCard.setCardLimit(20000);
        creditCard.setCardDebt(0);
        creditCard.setCurrency(Currency.TRY);
        creditCard.setCustomer(null);
    }

    @BeforeEach
    void setMockOutput() {
        creditCardService = new CreditCardServiceImpl(creditCardRepository, null, null);
        creditCardRepository.save(creditCard);
    }

    @Test
    void getCreditCardUserNameAndSurname() {
        Mockito.when(creditCardRepository.findCreditCardByCardAccountNumber(creditCard.getCardAccountNumber())).thenReturn(creditCard);
        Assertions.assertEquals("Isa SAYAR", creditCardService.getCreditCard(creditCard.getCardAccountNumber()).getCardNameSurname());
    }
}