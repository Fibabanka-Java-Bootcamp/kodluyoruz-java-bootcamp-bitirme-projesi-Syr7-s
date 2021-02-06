package org.kodluyoruz.mybank.card.creditcard.controller;


import org.kodluyoruz.mybank.card.bankcard.entity.BankCard;
import org.kodluyoruz.mybank.card.bankcard.service.BankCardService;
import org.kodluyoruz.mybank.card.creditcard.dto.CreditCardDto;
import org.kodluyoruz.mybank.card.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.exception.CreditCardNotCreatedException;
import org.kodluyoruz.mybank.card.creditcard.service.CreditCardService;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.service.CustomerService;
import org.kodluyoruz.mybank.extractofaccount.entity.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.service.ExtractOfAccountService;
import org.kodluyoruz.mybank.utilities.generate.accountgenerate.AccountGenerate;
import org.kodluyoruz.mybank.utilities.generate.securitycodegenerate.SecurityCodeGenerate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/card")
public class CreditCardController {
    private final CreditCardService creditCardService;
    private final CustomerService customerService;
    private final BankCardService bankCardService;
    private final ExtractOfAccountService extractOfAccountService;

    public CreditCardController(CreditCardService creditCardService, CustomerService customerService, BankCardService bankCardService, ExtractOfAccountService extractOfAccountService) {
        this.creditCardService = creditCardService;
        this.customerService = customerService;
        this.bankCardService = bankCardService;
        this.extractOfAccountService = extractOfAccountService;
    }

    @PostMapping("/{customerID}/creditCard")
    public CreditCardDto create(@PathVariable("customerID") long customerID
            , @RequestBody CreditCardDto creditCardDto) {
        creditCardDto.setCreditCardAccountNumber(Long.parseLong(AccountGenerate.generateAccount.get()));
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        creditCardDto.setCardNameSurname(customerDto.getCustomerName() + " " + customerDto.getCustomerLastname());
        creditCardDto.setSecurityCode(SecurityCodeGenerate.securityCode.get());
        creditCardDto.setCustomer(customerDto.toCustomer());
        return creditCardService.create(creditCardDto.toCreditCard()).toCreditCardDto();
    }

    @GetMapping(value = "/creditCards", params = {"page", "size"})
    public List<CreditCardDto> getAllCreditCard(@Min(value = 0) @RequestParam("page") int page, @Min(value = 1) @RequestParam("size") int size) {
        return creditCardService.creditCards(PageRequest.of(page, size)).stream()
                .map(CreditCard::toCreditCardDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{creditCardNo}")
    public ResponseEntity<CreditCardDto> get(@PathVariable("creditCardNo") long creditCardNo) {
        try {
            return ResponseEntity.ok(creditCardService.getCreditCard(creditCardNo).toCreditCardDto());
        } catch (CreditCardNotCreatedException exception) {
            return ResponseEntity.notFound().build();
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{creditCardNo}")
    @ResponseStatus(HttpStatus.OK)
    public CreditCardDto doShopping(@PathVariable("creditCardNo") long creditCardNo,
                                    @RequestParam("productPrice") int price) {
        return creditCardService.update(creditCardNo, price).toCreditCardDto();
    }

    @GetMapping("/{creditCardNo}/debt")
    public String creditCardDebtInquiry(@PathVariable("creditCardNo") long creditCardNo) {
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNo);
        double debt = creditCard.getExtractOfAccount().getTermDebt();
        double minimumPaymentAmount = creditCard.getExtractOfAccount().getMinimumPaymentAmount();
        String nameSurname = creditCard.getCardNameSurname();
        return nameSurname + " named customer debt : " + debt + " and minimum payment amount " + minimumPaymentAmount;
    }

    @PutMapping("/{bankCardNO}/debt/{creditCardNO}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardDto payCreditCardDebt(@PathVariable("bankCardNO") long bankCardNo,
                                           @PathVariable("creditCardNO") long creditCardNo,
                                           @Min(value = 4) @RequestParam("password") int password,
                                           @RequestParam("payMoney") int payMoney, @RequestParam("minimumPayment") double minimumPayment) {
      /*  BankCard bankCard = bankCardService.findBankCard(bankCardNo);
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNo);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        if (bankCard.getBankCardPassword() == password) {
            creditCard.setCardDebt(creditCard.getCardDebt() - payMoney);
            extractOfAccount.setTermDebt(Math.abs(extractOfAccount.getTermDebt() - payMoney));
            extractOfAccount.setOldDebt(extractOfAccount.getTermDebt() + (extractOfAccount.getTermDebt() * extractOfAccount.getBankRate()));
            extractOfAccount.setMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPayment));
            extractOfAccountService.update(extractOfAccount);
            return creditCardService.updateCard(creditCard).toCreditCardDto();
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"BankCard info is not correct.");
        }
*/
        return creditCardService.payCreditCardDebt(bankCardNo,creditCardNo,password,payMoney,minimumPayment).toCreditCardDto();
    }
}
