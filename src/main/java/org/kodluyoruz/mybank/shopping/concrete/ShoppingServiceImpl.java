package org.kodluyoruz.mybank.shopping.concrete;

import org.kodluyoruz.mybank.account.demanddepositaccount.abstrct.DemandDepositAccountService;
import org.kodluyoruz.mybank.account.demanddepositaccount.concrete.DemandDepositAccount;
import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardService;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCardServiceImpl;
import org.kodluyoruz.mybank.exchange.concrete.Exchange;
import org.kodluyoruz.mybank.extractofaccount.abstrct.ExtractOfAccountService;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.kodluyoruz.mybank.shopping.abstrct.ShoppingService;
import org.kodluyoruz.mybank.shopping.abstrct.ShoppingRepository;
import org.kodluyoruz.mybank.utilities.messages.ErrorMessages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShoppingServiceImpl implements ShoppingService<Shopping> {
    private final ShoppingRepository shoppingRepository;
    private final CreditCardService<CreditCard> creditCardService;
    private final ExtractOfAccountService<ExtractOfAccount> extractOfAccountService;
    private final DemandDepositAccountService<DemandDepositAccount> demandDepositAccountService;

    public ShoppingServiceImpl(ShoppingRepository shoppingRepository, CreditCardServiceImpl creditCardServiceImpl, ExtractOfAccountService<ExtractOfAccount> extractOfAccountService, DemandDepositAccountService<DemandDepositAccount> demandDepositAccountService) {
        this.shoppingRepository = shoppingRepository;
        this.creditCardService = creditCardServiceImpl;
        this.extractOfAccountService = extractOfAccountService;
        this.demandDepositAccountService = demandDepositAccountService;
    }

    @Override
    public Shopping create(Shopping shopping) {
        return shoppingRepository.save(shopping);
    }

    @Override
    public Shopping doShoppingByCreditCard(long creditCardNO, int password, ShoppingDto shoppingDto) {
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNO);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        if (creditCard.getCardPassword() == password) {
            creditCard.setCardDebt(creditCard.getCardDebt() + shoppingDto.getProductPrice());
            if (creditCard.getCardDebt() <= creditCard.getCardLimit()) {
                creditCardService.updateCard(creditCard);
                extractOfAccount.setTermDebt(creditCard.getCardDebt() + extractOfAccount.getTotalInterestAmount());
                extractOfAccount.setMinimumPaymentAmount(extractOfAccount.getTermDebt() * 0.3);
                extractOfAccountService.update(extractOfAccount);
                shoppingDto.setCreditCard(creditCard);
                return shoppingRepository.save(shoppingDto.toShopping());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.CREDIT_CARD_LIMIT_OVER);
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.CARD_PASSWORD_COULD_INCORRECT);
        }
    }

    @Override
    public Shopping doShoppingByBankCard(long bankCardAccountNumber, long demandDepositAccountNumber, int password, ShoppingDto shoppingDto) {
        DemandDepositAccount demandDepositAccount = demandDepositAccountService.get(demandDepositAccountNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found"));
        if (demandDepositAccount.getBankCard().getBankCardAccountNumber() == bankCardAccountNumber && demandDepositAccount.getBankCard().getBankCardPassword() == password) {
            double money = Exchange.convertProcess(shoppingDto.getCurrency(),demandDepositAccount.getDemandDepositAccountCurrency(),shoppingDto.getProductPrice());
            if (demandDepositAccount.getDemandDepositAccountBalance() - money < 0) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.NOT_ENOUGH_MONEY_IN_YOUR_ACCOUNT);
            } else {
                demandDepositAccount.setDemandDepositAccountBalance((int) (demandDepositAccount.getDemandDepositAccountBalance() - money));
                demandDepositAccountService.update(demandDepositAccount);
                return shoppingRepository.save(shoppingDto.toShopping());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.CARD_PASSWORD_COULD_INCORRECT);
        }

    }

    @Override
    public Shopping getShoppingByProductID(int productID) {
        Shopping shopping = shoppingRepository.findShoppingByProductID(productID);
        if (shopping != null) {
            return shopping;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.PRODUCT_COULD_NOT_FOUND);
        }
    }

    @Override
    public Page<Shopping> getAllShopping(Pageable pageable) {
        return shoppingRepository.findAll(pageable);
    }

}
