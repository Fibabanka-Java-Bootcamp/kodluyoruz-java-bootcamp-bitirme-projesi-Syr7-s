package org.kodluyoruz.mybank.creditcardshopping.service;

import org.kodluyoruz.mybank.card.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.service.CreditCardService;
import org.kodluyoruz.mybank.creditcardshopping.dto.ShoppingDto;
import org.kodluyoruz.mybank.creditcardshopping.entity.Shopping;
import org.kodluyoruz.mybank.creditcardshopping.repository.ShoppingRepository;
import org.kodluyoruz.mybank.extractofaccount.entity.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.service.ExtractOfAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ShoppingService {
    private final ShoppingRepository shoppingRepository;
    private final CreditCardService creditCardService;
    private final ExtractOfAccountService extractOfAccountService;
    public ShoppingService(ShoppingRepository shoppingRepository, CreditCardService creditCardService, ExtractOfAccountService extractOfAccountService) {
        this.shoppingRepository = shoppingRepository;
        this.creditCardService = creditCardService;
        this.extractOfAccountService = extractOfAccountService;
    }

    public Shopping create(Shopping shopping){
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
        }
    }
}
