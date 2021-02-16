package org.kodluyoruz.mybank.card.creditcard.abstrct;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CreditCardRepositoryTest {
    @Autowired
    CreditCardRepository creditCardRepository;
    @Test
    void findCreditCardByCardAccountNumber(){
        CreditCard creditCard = creditCardRepository.findCreditCardByCardAccountNumber(8518332901508354L);
        Assertions.assertEquals("Isa SAYAR",creditCard.getCardNameSurname());
    }
    @Test
    void isCreditCardLimitTenThousandControl(){
        CreditCard creditCard = creditCardRepository.findCreditCardByCardAccountNumber(8518332901508354L);
        Assertions.assertEquals(10000,creditCard.getCardLimit());
    }
}
