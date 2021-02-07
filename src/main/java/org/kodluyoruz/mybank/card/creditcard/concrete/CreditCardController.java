package org.kodluyoruz.mybank.card.creditcard.concrete;



import org.kodluyoruz.mybank.card.creditcard.abstrct.ICreditCardService;
import org.kodluyoruz.mybank.card.creditcard.exception.CreditCardNotCreatedException;
import org.kodluyoruz.mybank.customer.abstrct.ICustomerService;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.customer.concrete.CustomerDto;
import org.kodluyoruz.mybank.utilities.generate.accountgenerate.Account;
import org.kodluyoruz.mybank.utilities.generate.securitycodegenerate.SecurityCodeGenerate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/card")
public class CreditCardController {
    private final ICreditCardService<CreditCard> creditCardService;
    private final ICustomerService<Customer> customerService;


    public CreditCardController(ICreditCardService<CreditCard> creditCardService, ICustomerService<Customer> customerService) {
        this.creditCardService = creditCardService;
        this.customerService = customerService;
    }

    @PostMapping("/{customerTC}/creditCard")
    public CreditCardDto create(@PathVariable("customerTC") long customerTC
            , @RequestBody CreditCardDto creditCardDto) {
        creditCardDto.setCreditCardAccountNumber(Long.parseLong(Account.generateAccount.get()));
        CustomerDto customerDto = customerService.getCustomerById(customerTC).toCustomerDto();
        creditCardDto.setCardNameSurname(customerDto.getCustomerName() + " " + customerDto.getCustomerLastname());
        creditCardDto.setSecurityCode(SecurityCodeGenerate.securityCode.get());
        creditCardDto.setCustomer(customerDto.toCustomer());
        return creditCardService.create(creditCardDto.toCreditCard()).toCreditCardDto();
    }

    @GetMapping(value = "/creditCards", params = {"page", "size"})
    public List<CreditCardDto> getAllCreditCard(@Min(value = 0) @RequestParam("page") int page, @Min(value = 1) @RequestParam("size") int size) {
        return creditCardService.cards(PageRequest.of(page, size)).stream()
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


    @GetMapping("/{creditCardNO}/debt")
    public String creditCardDebtInquiry(@PathVariable("creditCardNO") long creditCardNO) {
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNO);
        double debt = creditCard.getExtractOfAccount().getTermDebt();
        double minimumPaymentAmount = creditCard.getExtractOfAccount().getMinimumPaymentAmount();
        String nameSurname = creditCard.getCardNameSurname();
        return nameSurname + " named customer debt : " + debt + " and minimum payment amount " + minimumPaymentAmount;
    }

    @PutMapping("/{bankCardNO}/debt/{creditCardNO}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardDto payCreditCardDebt(@PathVariable("bankCardNO") long bankCardNO,
                                           @PathVariable("creditCardNO") long creditCardNO,
                                           @Min(value = 4) @RequestParam("password") int password,
                                           @RequestParam("payMoney") int payMoney, @RequestParam("minimumPayment") double minimumPayment) {

        return creditCardService.payCreditCardDebt(bankCardNO, creditCardNO, password, payMoney, minimumPayment).toCreditCardDto();
    }
}
