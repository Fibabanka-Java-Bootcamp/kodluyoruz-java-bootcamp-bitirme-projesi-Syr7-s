package org.kodluyoruz.mybank.card.bankcard.concrete;

import org.kodluyoruz.mybank.card.bankcard.abstrct.BankCardService;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotDeletedException;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotFoundException;
import org.kodluyoruz.mybank.card.bankcard.abstrct.BankCardRepository;
import org.kodluyoruz.mybank.customer.abstrct.CustomerService;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.customer.concrete.CustomerDto;

import org.kodluyoruz.mybank.utilities.generate.accountgenerate.Account;
import org.kodluyoruz.mybank.utilities.generate.securitycodegenerate.SecurityCodeGenerate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class BankCardServiceImpl implements BankCardService<BankCard> {
    private final BankCardRepository bankCardRepository;
    private final CustomerService<Customer> customerService;

    public BankCardServiceImpl(BankCardRepository bankCardRepository, CustomerService<Customer> customerService) {
        this.bankCardRepository = bankCardRepository;
        this.customerService = customerService;
    }

    @Override
    public BankCard create(BankCard bankCard) {
        return bankCardRepository.save(bankCard);
    }

    @Override
    public BankCard create(long customerTC, BankCardDto bankCardDto) {
        CustomerDto customerDto = customerService.getCustomerById(customerTC).toCustomerDto();
        bankCardDto.setBankCardAccountNumber(Long.parseLong(Account.generateAccount.get()));
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

}
