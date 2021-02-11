package org.kodluyoruz.mybank.extractofaccount.concrete;

import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardService;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.extractofaccount.abstrct.ExtractOfAccountService;
import org.kodluyoruz.mybank.utilities.messages.ErrorMessages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/extractofaccount")
public class ExtractOfController {
    private final ExtractOfAccountService<ExtractOfAccount> extractOfAccountService;
    private final CreditCardService<CreditCard> creditCardService;

    public ExtractOfController(ExtractOfAccountService<ExtractOfAccount> extractOfAccountService, CreditCardService<CreditCard> creditCardService) {
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.CARD_PASSWORD_COULD_INCORRECT);
        }

    }

    @GetMapping("/{extractNO}/extract")
    public ExtractOfAccountDto get(@PathVariable("extractNO") int extractNO) {
        return extractOfAccountService.get(extractNO).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,ErrorMessages.EXTRACT_OF_ACCOUNT_COULD_NOT_FOUND)).toExtractOfAccountDto();
    }
}
