package org.kodluyoruz.mybank.shopping.concrete;

import org.kodluyoruz.mybank.shopping.abstrct.ShoppingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {
    private final ShoppingService<Shopping> shoppingService;

    public ShoppingController(ShoppingService<Shopping> shoppingService) {
        this.shoppingService = shoppingService;
    }

    @PostMapping("/{creditCardNO}")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingDto doShopping(@PathVariable("creditCardNO") long creditCardNO, @RequestParam("password") int password, @RequestBody ShoppingDto shoppingDto) {

        return shoppingService.doShoppingByCreditCard(creditCardNO, password, shoppingDto).toShoppingDto();
    }
}
