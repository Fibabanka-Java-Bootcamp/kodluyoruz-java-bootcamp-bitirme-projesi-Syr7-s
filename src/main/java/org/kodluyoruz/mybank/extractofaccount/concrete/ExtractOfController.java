package org.kodluyoruz.mybank.extractofaccount.concrete;

import org.kodluyoruz.mybank.card.creditcard.abstrct.ICreditCardService;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCardService;
import org.kodluyoruz.mybank.extractofaccount.abstrct.IExtractOfAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/extractofaccount")
public class ExtractOfController {
    private final IExtractOfAccountService<ExtractOfAccount> extractOfAccountService;
    private final ICreditCardService<CreditCard> creditCardService;

    public ExtractOfController(IExtractOfAccountService<ExtractOfAccount> extractOfAccountService,ICreditCardService<CreditCard> creditCardService) {
        this.extractOfAccountService = extractOfAccountService;
        this.creditCardService = creditCardService;
    }

    @PostMapping("/{creditCardNO}")
    @ResponseStatus(HttpStatus.CREATED)
    public ExtractOfAccountDto create(@PathVariable("creditCardNO") long creditCardNO, @RequestParam("password") int password,
                                   @RequestBody ExtractOfAccountDto extractOfAccountDto) {
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNO);
        if (creditCard.getCardPassword() == password) {
            extractOfAccountDto.setCreditCard(creditCard);
            return extractOfAccountService.create(extractOfAccountDto.toExtractOfAccount()).toExtractOfAccountDto();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CreditCard info is not correct.");
        }

    }

    @GetMapping("/{extractNO}/extract")
    public ExtractOfAccountDto get(@PathVariable("extractNO") int extractNO) {
        return extractOfAccountService.get(extractNO).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Extract is not found")).toExtractOfAccountDto();
    }
}
