package org.kodluyoruz.mybank.bankcard.service;

import org.kodluyoruz.mybank.bankcard.entity.BankCard;
import org.kodluyoruz.mybank.bankcard.exception.BankCardNotFoundException;
import org.kodluyoruz.mybank.bankcard.exception.BankCardNotMatchException;
import org.kodluyoruz.mybank.bankcard.repository.BankCardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class BankCardService {
    private final BankCardRepository bankCardRepository;

    public BankCardService(BankCardRepository bankCardRepository) {
        this.bankCardRepository = bankCardRepository;
    }

    public BankCard create(BankCard bankCard) {
        return bankCardRepository.save(bankCard);
    }

    public BankCard findBankCard(long bankCardNO) {
        BankCard bankCard = bankCardRepository.findBankCardByBankCardNO(bankCardNO);
        if (bankCard != null) {
            return bankCard;
        } else {
            throw new BankCardNotFoundException("BankCard not created from by Customer or Bank");
        }
    }

    public Page<BankCard> bankCardPage(Pageable pageable) {
        return bankCardRepository.findAll(pageable);
    }
}
