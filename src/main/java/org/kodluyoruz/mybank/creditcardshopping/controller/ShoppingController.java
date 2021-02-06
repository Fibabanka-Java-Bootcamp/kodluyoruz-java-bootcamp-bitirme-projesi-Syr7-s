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

        return shoppingService.doShoppingByCreditCard(creditCardNo,password,shoppingDto).toShoppingDto();
    }
}
