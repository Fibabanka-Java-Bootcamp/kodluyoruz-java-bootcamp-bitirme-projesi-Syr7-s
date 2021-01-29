package org.kodluyoruz.mybank.bankcard.service;

import org.kodluyoruz.mybank.bankcard.entity.BankCard;
import org.kodluyoruz.mybank.bankcard.repository.BankCardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class BankCardService {
    private final BankCardRepository bankCardRepository;

    public BankCardService(BankCardRepository bankCardRepository) {
        this.bankCardRepository = bankCardRepository;
    }

    public BankCard create(BankCard bankCard){
       return bankCardRepository.save(bankCard);
    }

    public BankCard findBankCard(long bankCardNO){
        BankCard bankCard = bankCardRepository.findBankCardByBankCardNO(bankCardNO);
        if (bankCard!=null){
            return bankCard;
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"BankCard is not found");
        }
    }
}
