package org.kodluyoruz.mybank.card.bankcard.service;

import org.kodluyoruz.mybank.card.bankcard.dto.BankCardDto;
import org.kodluyoruz.mybank.card.bankcard.entity.BankCard;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotDeletedException;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotFoundException;
import org.kodluyoruz.mybank.card.bankcard.repository.BankCardRepository;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.service.CustomerService;
import org.kodluyoruz.mybank.utilities.generate.accountgenerate.AccountGenerate;
import org.kodluyoruz.mybank.utilities.generate.securitycodegenerate.SecurityCodeGenerate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class BankCardService {
    private final BankCardRepository bankCardRepository;
    private final CustomerService customerService;

    public BankCardService(BankCardRepository bankCardRepository, CustomerService customerService) {
        this.bankCardRepository = bankCardRepository;
        this.customerService = customerService;
    }

    public BankCard create(BankCard bankCard) {
        return bankCardRepository.save(bankCard);
    }

    public BankCard create(long customerID, BankCardDto bankCardDto) {
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        bankCardDto.setBankCardAccountNumber(Long.parseLong(AccountGenerate.generateAccount.get()));
        bankCardDto.setBankCardNameSurname(customerDto.getCustomerName() + " " + customerDto.getCustomerLastname());
        bankCardDto.setSecurityCode(SecurityCodeGenerate.securityCode.get());
        return bankCardRepository.save(bankCardDto.toBankCard());
    }

    public BankCard findBankCard(long bankCardNO) {
        BankCard bankCard = bankCardRepository.findBankCardByBankCardAccountNumber(bankCardNO);
        if (bankCard != null) {
            return bankCard;
        } else {
            throw new BankCardNotFoundException("BankCard not created from by Customer or Bank");
        }
    }

    public Page<BankCard> bankCardPage(Pageable pageable) {
        return bankCardRepository.findAll(pageable);
    }

    public void delete(long bankCardNo) {
        BankCard bankCard = findBankCard(bankCardNo);
        try {
            bankCardRepository.delete(bankCard);
        } catch (BankCardNotDeletedException exception) {
            throw new BankCardNotDeletedException("BankCard not deleted.");
        }
    }


}
