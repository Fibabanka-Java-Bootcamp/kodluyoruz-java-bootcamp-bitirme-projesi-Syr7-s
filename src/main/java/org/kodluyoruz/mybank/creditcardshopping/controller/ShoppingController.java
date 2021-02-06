package org.kodluyoruz.mybank.creditcardshopping.controller;

import org.kodluyoruz.mybank.creditcardshopping.dto.ShoppingDto;
import org.kodluyoruz.mybank.creditcardshopping.service.ShoppingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {
    private final ShoppingService shoppingService;

    public ShoppingController(ShoppingService shoppingService) {
        this.shoppingService = shoppingService;
    }

    @PostMapping("/{creditCardNo}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingDto doShopping(@PathVariable("creditCardNo") long creditCardNo, @RequestParam("password") int password, @RequestBody ShoppingDto shoppingDto) {

        return shoppingService.doShoppingByCreditCard(creditCardNo, password, shoppingDto).toShoppingDto();
    }
}
