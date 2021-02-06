package org.kodluyoruz.mybank.creditcardshopping.controller;

import org.kodluyoruz.mybank.card.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.service.CreditCardService;
import org.kodluyoruz.mybank.creditcardshopping.dto.ShoppingDto;
import org.kodluyoruz.mybank.creditcardshopping.service.ShoppingService;
import org.kodluyoruz.mybank.extractofaccount.entity.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.service.ExtractOfAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {
    private final ShoppingService shoppingService;
    private final CreditCardService creditCardService;
    private final ExtractOfAccountService extractOfAccountService;

    public ShoppingController(ShoppingService shoppingService, CreditCardService creditCardService, ExtractOfAccountService extractOfAccountService) {
        this.shoppingService = shoppingService;
        this.creditCardService = creditCardService;
        this.extractOfAccountService = extractOfAccountService;
    }

    @PostMapping("/{creditCardNo}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingDto doShopping(@PathVariable("creditCardNo") long creditCardNo, @RequestParam("password") int password, @RequestBody ShoppingDto shoppingDto) {
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
                return shoppingService.create(shoppingDto.toShopping()).toShoppingDto();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CreditLimit is over.");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CreditCard password is not correct.");
        }
    }
}
