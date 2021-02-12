package org.kodluyoruz.mybank.shopping.concrete;

import org.kodluyoruz.mybank.shopping.abstrct.ShoppingService;
import org.kodluyoruz.mybank.utilities.messages.ErrorMessages;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import javax.validation.constraints.Min;
import java.net.URI;
import java.util.stream.Collectors;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shopping")
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.AN_ERROR_OCCURRED);
        }
    }

    @PostMapping("/{bankCardAccountNumber}/shopping/{demandDepositAccountNumber}")
    public ShoppingDto doShoppingWithBankCard(@PathVariable("bankCardAccountNumber") long accountNumber,
                                              @PathVariable("demandDepositAccountNumber") long demandDepositAccountNumber,
                                              @RequestParam("password") int password,
                                              @RequestBody ShoppingDto shoppingDto){
        return shoppingService.doShoppingByBankCard(accountNumber,demandDepositAccountNumber,password,shoppingDto).toShoppingDto();

    }
    @GetMapping("/{productID}")
    public ResponseEntity<ShoppingDto> getShoppingByProductID(@PathVariable("productID") int productID){
        try{
            return ResponseEntity.ok(shoppingService.getShoppingByProductID(productID).toShoppingDto());
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping(value = "/all/shoppings",params = {"page","size"})
    public List<ShoppingDto> getAllShopping(@Min(value = 0) @RequestParam("page") int page, @Min(value = 1) @RequestParam("size") int size){
        return shoppingService.getAllShopping(PageRequest.of(page, size)).stream()
                .map(Shopping::toShoppingDto)
                .collect(Collectors.toList());
    }
}
