package org.kodluyoruz.mybank.card.bankcard.concrete;

import org.apache.log4j.Logger;
import org.kodluyoruz.mybank.card.bankcard.abstrct.BankCardService;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotDeletedException;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotFoundException;
import org.kodluyoruz.mybank.card.bankcard.abstrct.BankCardRepository;
import org.kodluyoruz.mybank.customer.abstrct.CustomerService;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.customer.concrete.CustomerDto;

import org.kodluyoruz.mybank.utilities.generate.accountgenerate.Account;
import org.kodluyoruz.mybank.utilities.generate.cardaccountgenerate.CardAccountNumber;
import org.kodluyoruz.mybank.utilities.generate.securitycodegenerate.SecurityCodeGenerate;
import org.kodluyoruz.mybank.utilities.messages.ErrorMessages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class BankCardServiceImpl implements BankCardService<BankCard> {
    private final BankCardRepository bankCardRepository;
    private final CustomerService<Customer> customerService;
    private static final Logger log = Logger.getLogger(BankCardServiceImpl.class);

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
        bankCardDto.setBankCardAccountNumber(Long.parseLong(CardAccountNumber.generateCardAccountNumber.get()));
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
            log.error(ErrorMessages.CARD_COULD_NOT_DELETED);
            throw new BankCardNotFoundException(ErrorMessages.CARD_COULD_NOT_CREATED);
        }
    }

    @Override
    public Page<BankCard> bankCardPage(Pageable pageable) {
        return bankCardRepository.findAll(pageable);
    }

    @Override
    public String delete(long bankCardNo) {
        BankCard bankCard = findBankCard(bankCardNo);
        try {
            bankCardRepository.delete(bankCard);
            return  bankCard.getBankCardNameSurname() + " named customer canceled her bank card usage.";
        } catch (BankCardNotDeletedException exception) {
            log.error(ErrorMessages.CARD_COULD_NOT_DELETED);
            throw new BankCardNotDeletedException(ErrorMessages.CARD_COULD_NOT_DELETED);
        }
    }

}
