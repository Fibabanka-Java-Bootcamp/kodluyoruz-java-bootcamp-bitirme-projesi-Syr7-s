package org.kodluyoruz.mybank.shopping.concrete;

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
