package org.kodluyoruz.mybank.card.creditcard.concrete;

import org.kodluyoruz.mybank.card.bankcard.abstrct.BankCardService;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCard;
import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardService;
import org.kodluyoruz.mybank.card.creditcard.exception.CreditCardNotCreatedException;
import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardRepository;
import org.kodluyoruz.mybank.extractofaccount.abstrct.ExtractOfAccountService;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccountServiceImpl;
import org.kodluyoruz.mybank.utilities.debtprocess.Debt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CreditCardServiceImpl implements CreditCardService<CreditCard> {
    private final CreditCardRepository creditCardRepository;
    private final BankCardService<BankCard> bankCardService;
    private final ExtractOfAccountService<ExtractOfAccount> extractOfAccountService;

    public CreditCardServiceImpl(CreditCardRepository creditCardRepository, BankCardService<BankCard> bankCardService, ExtractOfAccountServiceImpl extractOfAccountServiceImpl) {
        this.creditCardRepository = creditCardRepository;
        this.bankCardService = bankCardService;
        this.extractOfAccountService = extractOfAccountServiceImpl;
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
    public CreditCard getCreditCard(long creditCardNO) {
        CreditCard creditCard = creditCardRepository.findCreditCardByCardAccountNumber(creditCardNO);
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
    public CreditCard payCreditCardDebt(long bankCardNO, long creditCardNO, int password, int payMoney, double minimumPayment) {
        BankCard bankCard = bankCardService.findBankCard(bankCardNO);
        CreditCard creditCard = getCreditCard(creditCardNO);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        if (bankCard.getBankCardPassword() == password) {
            Debt.debtProcess(payMoney, minimumPayment, creditCard, extractOfAccount);
            extractOfAccount.setOldDebt(extractOfAccount.getTermDebt());
            extractOfAccount.setMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPayment));
            extractOfAccountService.update(extractOfAccount);
            return creditCardRepository.save(creditCard);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BankCard info is not correct.");
        }
    }
}