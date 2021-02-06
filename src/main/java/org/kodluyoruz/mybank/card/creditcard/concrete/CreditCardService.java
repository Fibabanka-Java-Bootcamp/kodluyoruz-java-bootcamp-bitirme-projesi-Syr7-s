package org.kodluyoruz.mybank.card.creditcard.concrete;

import org.kodluyoruz.mybank.card.bankcard.concrete.BankCard;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCardService;
import org.kodluyoruz.mybank.card.creditcard.abstrct.ICreditCardService;
import org.kodluyoruz.mybank.card.creditcard.exception.CreditCardNotCreatedException;
import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardRepository;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CreditCardService implements ICreditCardService<CreditCard> {
    private final CreditCardRepository creditCardRepository;
    private final BankCardService bankCardService;
    private final ExtractOfAccountService extractOfAccountService;

    public CreditCardService(CreditCardRepository creditCardRepository, BankCardService bankCardService, ExtractOfAccountService extractOfAccountService) {
        this.creditCardRepository = creditCardRepository;
        this.bankCardService = bankCardService;
        this.extractOfAccountService = extractOfAccountService;
    }

    @Override
    public CreditCard create(CreditCard creditCard) {
        return creditCardRepository.save(creditCard);
    }

    @Override
    public Page<CreditCard> cards(Pageable pageable) {
        return creditCardRepository.findAll(pageable);
    }

    @Override
    public CreditCard getCreditCard(long creditCardNo) {
        CreditCard creditCard = creditCardRepository.findCreditCardByCardAccountNumber(creditCardNo);
        if (creditCard != null) {
            return creditCard;
        } else {
            throw new CreditCardNotCreatedException("CreditCard is not created.");
        }
    }

    @Override
    public CreditCard updateCard(CreditCard creditCard) {
        return creditCardRepository.save(creditCard);
    }

    @Override
    public CreditCard payCreditCardDebt(long bankCardNo, long creditCardNo, int password,int payMoney, double minimumPayment) {
        BankCard bankCard = bankCardService.findBankCard(bankCardNo);
        CreditCard creditCard = getCreditCard(creditCardNo);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        if (bankCard.getBankCardPassword() == password) {
            creditCard.setCardDebt(creditCard.getCardDebt() - payMoney);
            extractOfAccount.setTermDebt(Math.abs(extractOfAccount.getTermDebt() - payMoney));
            extractOfAccount.setOldDebt(extractOfAccount.getTermDebt() + (extractOfAccount.getTermDebt() * extractOfAccount.getBankRate()));
            extractOfAccount.setMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPayment));
            extractOfAccountService.update(extractOfAccount);
            return creditCardRepository.save(creditCard);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BankCard info is not correct.");
        }
    }


  /*  public CreditCard create(CreditCard creditCard) {
        return creditCardRepository.save(creditCard);
    }

    public Page<CreditCard> creditCards(Pageable pageable) {
        return creditCardRepository.findAll(pageable);
    }

    public CreditCard getCreditCard(long creditCardNo) {
        CreditCard creditCard = creditCardRepository.findCreditCardByCardAccountNumber(creditCardNo);
        if (creditCard != null) {
            return creditCard;
        } else {
            throw new CreditCardNotCreatedException("CreditCard is not created.");
        }
    }

    public CreditCard updateCard(CreditCard creditCard) {
        return creditCardRepository.save(creditCard);
    }


    public CreditCard payCreditCardDebt(long bankCardNo, long creditCardNo, int password, int payMoney, double minimumPayment) {

        BankCard bankCard = bankCardService.findBankCard(bankCardNo);
        CreditCard creditCard = getCreditCard(creditCardNo);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        if (bankCard.getBankCardPassword() == password) {
            creditCard.setCardDebt(creditCard.getCardDebt() - payMoney);
            extractOfAccount.setTermDebt(Math.abs(extractOfAccount.getTermDebt() - payMoney));
            extractOfAccount.setOldDebt(extractOfAccount.getTermDebt() + (extractOfAccount.getTermDebt() * extractOfAccount.getBankRate()));
            extractOfAccount.setMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPayment));
            extractOfAccountService.update(extractOfAccount);
            return creditCardRepository.save(creditCard);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BankCard info is not correct.");
        }
    }*/
}
