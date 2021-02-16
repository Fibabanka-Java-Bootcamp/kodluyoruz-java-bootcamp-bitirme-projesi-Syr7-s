package org.kodluyoruz.mybank.extractofaccount.concrete;

import org.apache.log4j.Logger;
import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardService;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.extractofaccount.abstrct.ExtractOfAccountService;
import org.kodluyoruz.mybank.utilities.enums.messages.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/extractofaccount")
public class ExtractOfAccountController {
    private final ExtractOfAccountService<ExtractOfAccount> extractOfAccountService;
    private final CreditCardService<CreditCard> creditCardService;
    private static final Logger log = Logger.getLogger(ExtractOfAccountController.class);

    public ExtractOfAccountController(ExtractOfAccountService<ExtractOfAccount> extractOfAccountService, CreditCardService<CreditCard> creditCardService) {
        this.extractOfAccountService = extractOfAccountService;
        this.creditCardService = creditCardService;
    }

    @PostMapping("/{creditCardNO}")
    @ResponseStatus(HttpStatus.CREATED)
    public ExtractOfAccountDto create(@PathVariable("creditCardNO") long creditCardNO, @RequestParam("password") int password,
                                      @RequestBody ExtractOfAccountDto extractOfAccountDto) {
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNO);
        if (creditCard.getCardPassword() == password) {
            log.info("Extract of account create for credit card.");
            extractOfAccountDto.setCreditCard(creditCard);
            return extractOfAccountService.create(extractOfAccountDto.toExtractOfAccount()).toExtractOfAccountDto();
        } else {
            log.error(Messages.Error.CARD_PASSWORD_COULD_INCORRECT.message);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.CARD_PASSWORD_COULD_INCORRECT.message);
        }

    }

    @GetMapping("/{extractNO}/extract")
    public ExtractOfAccountDto get(@PathVariable("extractNO") int extractNO) {
        log.info("extract of account info will get.");
        return extractOfAccountService.get(extractNO).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.EXTRACT_OF_ACCOUNT_COULD_NOT_FOUND.message)).toExtractOfAccountDto();
    }
}
