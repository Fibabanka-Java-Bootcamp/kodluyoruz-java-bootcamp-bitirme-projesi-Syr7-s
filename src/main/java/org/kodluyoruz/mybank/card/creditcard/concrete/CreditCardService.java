package org.kodluyoruz.mybank.card.creditcard.concrete;

import org.kodluyoruz.mybank.card.bankcard.abstrct.IBankCardService;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCard;
import org.kodluyoruz.mybank.card.creditcard.abstrct.ICreditCardService;
import org.kodluyoruz.mybank.card.creditcard.exception.CreditCardNotCreatedException;
import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardRepository;
import org.kodluyoruz.mybank.extractofaccount.abstrct.IExtractOfAccountService;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccountService;
import org.kodluyoruz.mybank.utilities.debtprocess.Debt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CreditCardService implements ICreditCardService<CreditCard> {
    private final CreditCardRepository creditCardRepository;
    private final IBankCardService<BankCard> bankCardService;
    private final IExtractOfAccountService<ExtractOfAccount> extractOfAccountService;

    public CreditCardService(CreditCardRepository creditCardRepository, IBankCardService<BankCard> bankCardService, ExtractOfAccountService extractOfAccountService) {
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
    public CreditCard payCreditCardDebt(long bankCardNo, long creditCardNo, int password, int payMoney, double minimumPayment) {
        BankCard bankCard = bankCardService.findBankCard(bankCardNo);
        CreditCard creditCard = getCreditCard(creditCardNo);
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
/*
    public static void debtProcess(int payMoney, double minimumPayment, CreditCard creditCard, ExtractOfAccount extractOfAccount) {
        if (payMoney == 0) {
            extracted(minimumPayment, extractOfAccount);
            creditCard.setCardDebt((int) (creditCard.getCardDebt() + extractOfAccount.getTotalInterestAmount()));
            extractOfAccount.setTermDebt(creditCard.getCardDebt());
            extractOfAccount.setOldMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPayment));
        } else {
            creditCard.setCardDebt((int) ((creditCard.getCardDebt() + extractOfAccount.getTotalInterestAmount()) - payMoney));
            extractOfAccount.setTermDebt(Math.abs(extractOfAccount.getTermDebt() - payMoney));
            zeroSet(extractOfAccount);
        }
    }

    public static void zeroSet(ExtractOfAccount extractOfAccount) {
        extractOfAccount.setShoppingInterestAmount(0);
        extractOfAccount.setShoppingInterestAmountNext(0);
        extractOfAccount.setLateInterestAmount(0);
        extractOfAccount.setTotalInterestAmount(0);
        extractOfAccount.setOldMinimumPaymentAmount(0);
    }

    public static void extracted(double minimumPayment, ExtractOfAccount extractOfAccount) {
        double shoppingInterestAmount = ((extractOfAccount.getTermDebt() - minimumPayment) *
                extractOfAccount.getShoppingInterestRate() * (10 / 30.0)) / 100;
        double lateInterestAmount = ((extractOfAccount.getMinimumPaymentAmount() - minimumPayment) *
                extractOfAccount.getLateInterestRate() * (20 / 30.0)) / 100;
        double shoppingInterestAmountNext = ((extractOfAccount.getTermDebt() - extractOfAccount.getMinimumPaymentAmount()) *
                extractOfAccount.getShoppingInterestRate() * (20 / 30.0) / 100);
        extractOfAccount.setShoppingInterestAmount(shoppingInterestAmount);
        extractOfAccount.setLateInterestAmount(lateInterestAmount);
        extractOfAccount.setShoppingInterestAmountNext(shoppingInterestAmountNext);
        extractOfAccount.setTotalInterestAmount(shoppingInterestAmount + lateInterestAmount + shoppingInterestAmountNext);
    }*/
}