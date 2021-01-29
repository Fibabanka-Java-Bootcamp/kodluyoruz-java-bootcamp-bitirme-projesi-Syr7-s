package org.kodluyoruz.mybank.creditcard.controller;

import lombok.Getter;
import org.kodluyoruz.mybank.creditcard.dto.CreditCardDto;
import org.kodluyoruz.mybank.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.creditcard.service.CreditCardService;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.service.CustomerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/card")
public class CreditCardController {
    private final CreditCardService creditCardService;
    private final CustomerService customerService;
    public CreditCardController(CreditCardService creditCardService, CustomerService customerService) {
        this.creditCardService = creditCardService;
        this.customerService = customerService;
    }

    @PostMapping("/{customerID}/creditCard")
    public CreditCardDto create(@PathVariable("customerID") long customerID
    , @RequestBody CreditCardDto creditCardDto){
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        creditCardDto.setCardNameSurnname(customerDto.getCustomerName()+" "+customerDto.getCustomerLastname());
        creditCardDto.setCustomer(customerDto.toCustomer());
        return creditCardService.create(creditCardDto.toCreditCard()).toCreditCardDto();
    }

    @GetMapping(value = "/creditCards",params = {"page","size"})
    public List<CreditCardDto> getAllCreditCard(@Min(value = 0) @RequestParam("page") int page,@Min(value = 1) @RequestParam("size") int size){
        return creditCardService.creditCards(PageRequest.of(page,size)).stream()
                .map(CreditCard::toCreditCardDto)
                .collect(Collectors.toList());
    }
}
