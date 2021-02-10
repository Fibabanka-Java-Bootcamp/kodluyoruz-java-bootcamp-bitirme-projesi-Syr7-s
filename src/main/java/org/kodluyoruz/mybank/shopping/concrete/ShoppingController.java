package org.kodluyoruz.mybank.shopping.concrete;

import org.kodluyoruz.mybank.shopping.abstrct.ShoppingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {
    private final ShoppingService<Shopping> shoppingService;

    public ShoppingController(ShoppingService<Shopping> shoppingService) {
        this.shoppingService = shoppingService;
    }

    @PostMapping("/{creditCardNO}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<URI> doShopping(@PathVariable("creditCardNO") long creditCardNO, @RequestParam("password") int password, @RequestBody ShoppingDto shoppingDto) {
        try{
            ShoppingDto editedShopping = shoppingService.doShoppingByCreditCard(creditCardNO,password,shoppingDto).toShoppingDto();
            URI location = ServletUriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/shopping")
                    .path("/{productID}")
                    .buildAndExpand(editedShopping.getProductID())
                    .toUri();
            return ResponseEntity.created(location).build();
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"An Error Occurred");
        }
        //return shoppingService.doShoppingByCreditCard(creditCardNO, password, shoppingDto).toShoppingDto();
    }
    @GetMapping("/{productID}")
    public ResponseEntity<ShoppingDto> getShoppingByProductID(@PathVariable("productID") int productID){
        try{
            return ResponseEntity.ok(shoppingService.getShoppingByProductID(productID).toShoppingDto());
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
