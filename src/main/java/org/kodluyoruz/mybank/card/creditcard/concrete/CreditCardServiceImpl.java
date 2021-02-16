package org.kodluyoruz.mybank.card.creditcard.concrete;


import org.apache.log4j.Logger;
import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardService;
import org.kodluyoruz.mybank.card.creditcard.exception.CreditCardNotCreatedException;
import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardRepository;
import org.kodluyoruz.mybank.extractofaccount.abstrct.ExtractOfAccountService;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccountServiceImpl;
import org.kodluyoruz.mybank.utilities.debtprocess.Debt;
import org.kodluyoruz.mybank.utilities.enums.messages.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CreditCardServiceImpl implements CreditCardService<CreditCard> {
    private final CreditCardRepository creditCardRepository;
    private final ExtractOfAccountService<ExtractOfAccount> extractOfAccountService;
    private static final Logger log = Logger.getLogger(CreditCardServiceImpl.class);

    public CreditCardServiceImpl(CreditCardRepository creditCardRepository, ExtractOfAccountServiceImpl extractOfAccountServiceImpl) {
        this.creditCardRepository = creditCardRepository;
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
            log.error(Messages.Error.CARD_COULD_NOT_CREATED.message);
            throw new CreditCardNotCreatedException(Messages.Error.CARD_COULD_NOT_CREATED.message);
        }
    }

    @Override
    public CreditCard updateCard(CreditCard creditCard) {
        return creditCardRepository.save(creditCard);
    }

    @Override
    public String delete(long accountNumber) {
        CreditCard creditCard = getCreditCard(accountNumber);
        if (creditCard.getCardDebt() == 0) {
            creditCardRepository.delete(creditCard);
            return creditCard.getCardNameSurname()  + Messages.Info.NAMED_CUSTOMER_CANCELED_BANK_CARD_USAGE.message;
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.CARD_COULD_NOT_DELETED.message);
        }
    }

    @Override
    public CreditCard payCreditCardDebt(long creditCardNO, int password, int payMoney, double minimumPayment) {
        CreditCard creditCard = getCreditCard(creditCardNO);
        if (creditCard.getCardPassword() == password) {
            ExtractOfAccount extractOfAccount = getExtractOfAccount(payMoney, minimumPayment, creditCard);
            extractOfAccountService.update(extractOfAccount);
            return creditCardRepository.save(creditCard);
        } else {
            log.error(Messages.Error.CARD_PASSWORD_COULD_INCORRECT.message);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.CARD_PASSWORD_COULD_INCORRECT.message);
        }
    }

    @Override
    public CreditCard debtPaymentWithoutCreditCard(long creditCardAccountNO, int payMoney, double minimumPayment) {
        CreditCard creditCard = getCreditCard(creditCardAccountNO);
        ExtractOfAccount extractOfAccount = getExtractOfAccount(payMoney, minimumPayment, creditCard);
        extractOfAccountService.update(extractOfAccount);
        return creditCardRepository.save(creditCard);

    }

    private ExtractOfAccount getExtractOfAccount(int payMoney, double minimumPayment, CreditCard creditCard) {
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        Debt.debtProcess(payMoney, minimumPayment, creditCard, extractOfAccount);
        extractOfAccount.setOldDebt(extractOfAccount.getTermDebt());
        extractOfAccount.setMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPayment));
        return extractOfAccount;
    }
}