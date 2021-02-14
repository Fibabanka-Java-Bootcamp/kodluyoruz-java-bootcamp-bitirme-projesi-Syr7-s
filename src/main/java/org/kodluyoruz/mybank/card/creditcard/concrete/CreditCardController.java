package org.kodluyoruz.mybank.card.creditcard.concrete;


import org.apache.log4j.Logger;
import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardService;
import org.kodluyoruz.mybank.card.creditcard.exception.CreditCardNotCreatedException;
import org.kodluyoruz.mybank.customer.abstrct.CustomerService;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.customer.concrete.CustomerDto;
import org.kodluyoruz.mybank.utilities.generate.accountgenerate.Account;
import org.kodluyoruz.mybank.utilities.generate.cardaccountgenerate.CardAccountNumber;
import org.kodluyoruz.mybank.utilities.generate.securitycodegenerate.SecurityCodeGenerate;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/card")
public class CreditCardController {
    private final CreditCardService<CreditCard> creditCardService;
    private final CustomerService<Customer> customerService;
    private static final Logger log = Logger.getLogger(CreditCardController.class);

    public CreditCardController(CreditCardService<CreditCard> creditCardService, CustomerService<Customer> customerService) {
        this.creditCardService = creditCardService;
        this.customerService = customerService;
    }

    @PostMapping("/{customerTC}/creditCard")
    public CreditCardDto create(@PathVariable("customerTC") long customerTC
            , @RequestBody CreditCardDto creditCardDto) {
        creditCardDto.setCreditCardAccountNumber(Long.parseLong(CardAccountNumber.generateCardAccountNumber.get()));
        CustomerDto customerDto = customerService.getCustomerById(customerTC).toCustomerDto();
        creditCardDto.setCardNameSurname(customerDto.getCustomerName() + " " + customerDto.getCustomerLastname());
        creditCardDto.setSecurityCode(SecurityCodeGenerate.securityCode.get());
        creditCardDto.setCustomer(customerDto.toCustomer());
        log.info("Credit card will create.");
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
            log.info("Credit card info will get.");
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

    @PutMapping("/debt/{creditCardNO}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardDto payCreditCardDebt(@PathVariable("creditCardNO") long creditCardNO,
                                           @Min(value = 4) @RequestParam("password") int password,
                                           @RequestParam("payMoney") int payMoney,
                                           @RequestParam("minimumPayment") double minimumPayment) {
        log.info("Debt will payment with credit card where in ATM.");
        return creditCardService.payCreditCardDebt(creditCardNO, password, payMoney, minimumPayment).toCreditCardDto();
    }

    @PutMapping("/withoutCard/{creditCardNO}")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardDto debtPaymentWithoutCreditCard(@PathVariable("creditCardNO") long creditCardNO,
                                                      @RequestParam("payMoney") int payMoney,
                                                      @RequestParam("minimumPayment") double minimumPayment) {
        log.info("Debt will payment without credit card in ATM.");
        return creditCardService.debtPaymentWithoutCreditCard(creditCardNO, payMoney, minimumPayment).toCreditCardDto();
    }
}
