package org.kodluyoruz.mybank.bankcard.controller;

import org.kodluyoruz.mybank.bankcard.dto.BankCardDto;
import org.kodluyoruz.mybank.bankcard.service.BankCardService;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.entity.Customer;
import org.kodluyoruz.mybank.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bankcard")
public class BankCardController {
    private final BankCardService bankCardService;
    private final CustomerService customerService;

    public BankCardController(BankCardService bankCardService, CustomerService customerService) {
        this.bankCardService = bankCardService;
        this.customerService = customerService;
    }

    @PostMapping("/{customerID}")
    @ResponseStatus(HttpStatus.CREATED)
    public BankCardDto create(@PathVariable("customerID") long customerID,@RequestBody BankCardDto bankCardDto){
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        bankCardDto.setBankCardNameSurname(customerDto.getCustomerName()+" "+customerDto.getCustomerLastname());
        return bankCardService.create(bankCardDto.toBankCard()).toBankCardDto();
    }
}
