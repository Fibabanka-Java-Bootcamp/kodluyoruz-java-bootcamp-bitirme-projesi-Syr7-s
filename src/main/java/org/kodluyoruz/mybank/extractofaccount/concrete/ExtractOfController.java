package org.kodluyoruz.mybank.extractofaccount.concrete;

import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/extractofaccount")
public class ExtractOfController {
    private final ExtractOfAccountService extractOfAccountService;
    private final CreditCardService creditCardService;

    public ExtractOfController(ExtractOfAccountService extractOfAccountService,CreditCardService creditCardService) {
        this.extractOfAccountService = extractOfAccountService;
        this.creditCardService = creditCardService;
    }

    @PostMapping("/{creditCardNo}")
    @ResponseStatus(HttpStatus.CREATED)
    public ExtractOfAccountDto create(@PathVariable("creditCardNo") long creditCardNo, @RequestParam("password") int password,
                                   @RequestBody ExtractOfAccountDto extractOfAccountDto) {
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNo);
        if (creditCard.getCardPassword() == password) {
            extractOfAccountDto.setTermDebt(creditCard.getCardDebt());
            extractOfAccountDto.setOldDebt(extractOfAccountDto.getMinimumPaymentAmount());
            extractOfAccountDto.setMinimumPaymentAmount(creditCard.getCardDebt() * 0.3);
            extractOfAccountDto.setCreditCard(creditCard);
            return extractOfAccountService.create(extractOfAccountDto.toExtractOfAccount()).toExtractOfAccountDto();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CreditCard info is not correct.");
        }

    }

    @GetMapping("/{extractNo}/extract")
    public ExtractOfAccountDto get(@PathVariable("extractNo") int extractNo) {
        return extractOfAccountService.get(extractNo).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Extract is not found")).toExtractOfAccountDto();
    }
}
