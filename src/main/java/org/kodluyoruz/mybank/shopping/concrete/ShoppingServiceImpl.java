package org.kodluyoruz.mybank.shopping.concrete;

import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardService;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCardServiceImpl;
import org.kodluyoruz.mybank.extractofaccount.abstrct.ExtractOfAccountService;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.kodluyoruz.mybank.shopping.abstrct.ShoppingService;
import org.kodluyoruz.mybank.shopping.abstrct.ShoppingRepository;
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

    public ShoppingServiceImpl(ShoppingRepository shoppingRepository, CreditCardServiceImpl creditCardServiceImpl, ExtractOfAccountService<ExtractOfAccount> extractOfAccountService) {
        this.shoppingRepository = shoppingRepository;
        this.creditCardService = creditCardServiceImpl;
        this.extractOfAccountService = extractOfAccountService;
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CreditLimit is over.");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CreditCard password is not correct.");
        }
    }

    @Override
    public Shopping getShoppingByProductID(int productID) {
        Shopping shopping = shoppingRepository.findShoppingByProductID(productID);
        if (shopping != null){
            return shopping;
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Product not found");
        }
    }

    /*@Override
    public Page<Shopping> getAllShopping(Pageable pageable) {
        return shoppingRepository.findAll(pageable);
    }*/

}
