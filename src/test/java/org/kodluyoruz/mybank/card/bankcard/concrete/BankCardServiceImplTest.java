package org.kodluyoruz.mybank.card.bankcard.concrete;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kodluyoruz.mybank.card.bankcard.abstrct.BankCardRepository;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.customer.concrete.CustomerServiceImpl;
import org.kodluyoruz.mybank.utilities.generate.accountgenerate.Account;
import org.kodluyoruz.mybank.utilities.generate.securitycodegenerate.SecurityCodeGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@SpringBootTest
class BankCardServiceImplTest {
    @Autowired
    BankCardRepository bankCardRepository;

    @Autowired
    CustomerServiceImpl customerService;

    RestTemplate restTemplate;

    BankCard bankCard = new BankCard();
    Customer customer;
    @BeforeEach
    void setUp(){
        restTemplate = new RestTemplate();
        customer = customerService.getCustomerById(22433513943L);
    }
    @Test
    void create() {
        bankCard.setBankCardAccountNumber(Long.parseLong(Account.generateAccount.get()));
        bankCard.setBankCardNameSurname(customer.getCustomerName()+" "+customer.getCustomerLastname());
        bankCard.setBankCardPassword(1996);
        bankCard.setBankCardExpirationDate(LocalDate.of(2020,2,10));
        bankCard.setBankCardSecurityCode(SecurityCodeGenerate.securityCode.get());
        BankCard newBankCard = bankCardRepository.save(bankCard);
        Assertions.assertEquals(newBankCard.getBankCardNameSurname(),bankCard.getBankCardNameSurname());

    }
}