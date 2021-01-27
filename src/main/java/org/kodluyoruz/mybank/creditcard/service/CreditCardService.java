package org.kodluyoruz.mybank.creditcard.service;

import org.kodluyoruz.mybank.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.creditcard.repository.CreditCardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class CreditCardService {
    private final CreditCardRepository creditCardRepository;

    public CreditCardService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public CreditCard create(CreditCard creditCard){
        return creditCardRepository.save(creditCard);
    }

    public Page<CreditCard> creditCards(Pageable pageable){
        return creditCardRepository.findAll(pageable);
    }
}
