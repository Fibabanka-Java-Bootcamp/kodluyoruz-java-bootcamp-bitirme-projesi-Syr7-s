package org.kodluyoruz.mybank.shopping.concrete;

import org.kodluyoruz.mybank.card.creditcard.abstrct.ICreditCardService;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCardService;
import org.kodluyoruz.mybank.extractofaccount.abstrct.IExtractOfAccountService;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.kodluyoruz.mybank.shopping.abstrct.IShoppingService;
import org.kodluyoruz.mybank.shopping.abstrct.ShoppingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShoppingService implements IShoppingService<Shopping> {
    private final ShoppingRepository shoppingRepository;
    private final ICreditCardService<CreditCard> creditCardService;
    private final IExtractOfAccountService<ExtractOfAccount> extractOfAccountService;

    public ShoppingService(ShoppingRepository shoppingRepository, CreditCardService creditCardService, IExtractOfAccountService<ExtractOfAccount> extractOfAccountService) {
        this.shoppingRepository = shoppingRepository;
        this.creditCardService = creditCardService;
        this.extractOfAccountService = extractOfAccountService;
    }

    @Override
    public Shopping create(Shopping shopping) {
        return shoppingRepository.save(shopping);
    }

    @Override
    public Shopping doShoppingByCreditCard(long creditCardNo, int password, ShoppingDto shoppingDto) {
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNo);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        if (creditCard.getCardPassword() == password) {
            creditCard.setCardDebt(creditCard.getCardDebt() + shoppingDto.getProductPrice());
            if (creditCard.getCardDebt() <= creditCard.getCardLimit()) {
                creditCardService.updateCard(creditCard);
                extractOfAccount.setTermDebt(creditCard.getCardDebt());
                extractOfAccount.setMinimumPaymentAmount(extractOfAccount.getTermDebt() * 0.3);
                extractOfAccountService.update(extractOfAccount);
                shoppingDto.setCreditCard(creditCard);
                return shoppingRepository.save(shoppingDto.toShopping());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CreditLimit is over.");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CreditCard password is not correct.");
        }
    }

   /* public Shopping create(Shopping shopping){
        return shoppingRepository.save(shopping);
    }

    public Shopping doShoppingByCreditCard(long creditCardNo, int password, ShoppingDto shoppingDto){
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNo);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        if (creditCard.getCardPassword() == password) {
            creditCard.setCardDebt(creditCard.getCardDebt() + shoppingDto.getProductPrice());
            if (creditCard.getCardDebt() <= creditCard.getCardLimit()) {
                creditCardService.updateCard(creditCard);
                extractOfAccount.setTermDebt(creditCard.getCardDebt());
                extractOfAccount.setMinimumPaymentAmount(extractOfAccount.getTermDebt() * 0.3);
                extractOfAccountService.update(extractOfAccount);
                shoppingDto.setCreditCard(creditCard);
                return shoppingRepository.save(shoppingDto.toShopping());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CreditLimit is over.");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CreditCard password is not correct.");
        }*/
    //}
}
