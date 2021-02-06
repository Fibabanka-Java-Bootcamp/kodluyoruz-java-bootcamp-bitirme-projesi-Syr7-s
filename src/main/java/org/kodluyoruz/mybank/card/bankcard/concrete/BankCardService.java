package org.kodluyoruz.mybank.card.bankcard.concrete;

import org.kodluyoruz.mybank.card.bankcard.abstrct.IBankCardService;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotDeletedException;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotFoundException;
import org.kodluyoruz.mybank.card.bankcard.abstrct.BankCardRepository;
import org.kodluyoruz.mybank.customer.abstrct.ICustomerService;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.customer.concrete.CustomerDto;

import org.kodluyoruz.mybank.utilities.generate.accountgenerate.AccountGenerate;
import org.kodluyoruz.mybank.utilities.generate.securitycodegenerate.SecurityCodeGenerate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class BankCardService implements IBankCardService<BankCard> {
    private final BankCardRepository bankCardRepository;
    private final ICustomerService<Customer> customerService;

    public BankCardService(BankCardRepository bankCardRepository, ICustomerService<Customer> customerService) {
        this.bankCardRepository = bankCardRepository;
        this.customerService = customerService;
    }

    @Override
    public BankCard create(BankCard bankCard) {
        return bankCardRepository.save(bankCard);
    }

    @Override
    public BankCard create(long customerId, BankCardDto bankCardDto) {
        CustomerDto customerDto = customerService.getCustomerById(customerId).toCustomerDto();
        bankCardDto.setBankCardAccountNumber(Long.parseLong(AccountGenerate.generateAccount.get()));
        bankCardDto.setBankCardNameSurname(customerDto.getCustomerName() + " " + customerDto.getCustomerLastname());
        bankCardDto.setSecurityCode(SecurityCodeGenerate.securityCode.get());
        return bankCardRepository.save(bankCardDto.toBankCard());
    }

    @Override
    public BankCard findBankCard(long bankCardNo) {
        BankCard bankCard = bankCardRepository.findBankCardByBankCardAccountNumber(bankCardNo);
        if (bankCard != null) {
            return bankCard;
        } else {
            throw new BankCardNotFoundException("BankCard not created from by Customer or Bank");
        }
    }

    @Override
    public Page<BankCard> bankCardPage(Pageable pageable) {
        return bankCardRepository.findAll(pageable);
    }

    @Override
    public void delete(long bankCardNo) {
        BankCard bankCard = findBankCard(bankCardNo);
        try {
            bankCardRepository.delete(bankCard);
        } catch (BankCardNotDeletedException exception) {
            throw new BankCardNotDeletedException("BankCard not deleted.");
        }
    }
/*
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
*/

}
