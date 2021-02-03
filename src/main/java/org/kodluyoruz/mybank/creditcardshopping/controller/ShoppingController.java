package org.kodluyoruz.mybank.creditcardshopping.controller;

import org.kodluyoruz.mybank.card.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.service.CreditCardService;
import org.kodluyoruz.mybank.creditcardshopping.dto.ShoppingDto;
import org.kodluyoruz.mybank.creditcardshopping.service.ShoppingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {
    private final ShoppingService shoppingService;
    private final CreditCardService creditCardService;

    public ShoppingController(ShoppingService shoppingService, CreditCardService creditCardService) {
        this.shoppingService = shoppingService;
        this.creditCardService = creditCardService;
    }

    @PostMapping("/{creditCardNo}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingDto doShopping(@PathVariable("creditCardNo") long creditCardNo,@RequestParam("password") int password, @RequestBody ShoppingDto shoppingDto) {
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNo);
        if (creditCard.getCardPassword() == password){
            creditCard.setCardDebt(creditCard.getCardDebt()+shoppingDto.getProductPrice());
            creditCardService.updateCard(creditCard);
            shoppingDto.setCreditCard(creditCard);
            return shoppingService.create(shoppingDto.toShopping()).toShoppingDto();
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"CreditCard is not found.");
        }
    }
}
