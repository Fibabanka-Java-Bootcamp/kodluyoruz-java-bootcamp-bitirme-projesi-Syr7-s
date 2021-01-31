package org.kodluyoruz.mybank.creditcard.service;

import org.kodluyoruz.mybank.creditcard.dto.CreditCardDto;
import org.kodluyoruz.mybank.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.creditcard.exception.CreditCardNotCreatedException;
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

    public CreditCard create(CreditCard creditCard) {
        return creditCardRepository.save(creditCard);
    }

    public Page<CreditCard> creditCards(Pageable pageable) {
        return creditCardRepository.findAll(pageable);
    }

    public CreditCard getCreditCard(long creditCardNo) {
        CreditCard creditCard = creditCardRepository.findCreditCardByCardNO(creditCardNo);
        if (creditCard != null){
            return creditCard;
        }else{
            throw new CreditCardNotCreatedException("CreditCard is not created.");
        }
    }
    public CreditCard updateCard(CreditCard creditCard){
        return creditCardRepository.save(creditCard);
    }
    public CreditCard update(long creditCardNo,int price){
        CreditCard creditCard = getCreditCard(creditCardNo);
        int money = creditCard.getCardLimit();
        if (money - price < 0) {
            int debtMoney = money - price;
            //Credit Card Limit asildi.
            //creditCard.setCardLimit(10000);
            creditCard.setCardDebt(Math.abs(debtMoney));
        } else {
            //int cardLimitMoney = creditCard.getCardLimit();
            //creditCard.setCardLimit(cardLimitMoney - (money - price));
            creditCard.setCardDebt(money - price);

        }
        return creditCardRepository.save(creditCard);
    }
}
