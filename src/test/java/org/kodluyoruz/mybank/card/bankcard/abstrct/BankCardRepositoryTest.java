package org.kodluyoruz.mybank.card.bankcard.abstrct;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BankCardRepositoryTest {
    @Autowired
    BankCardRepository bankCardRepository;

    @Test
    void findBankCardByBankCardAccountNumber(){
        BankCard bankCard = bankCardRepository.findBankCardByBankCardAccountNumber(8536797341143025L);
        Assertions.assertEquals("Isa SAYAR",bankCard.getBankCardNameSurname());
    }

}
