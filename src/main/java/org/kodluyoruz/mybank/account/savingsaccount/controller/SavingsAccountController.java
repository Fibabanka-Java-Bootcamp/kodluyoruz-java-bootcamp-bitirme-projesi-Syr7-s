package org.kodluyoruz.mybank.account.savingsaccount.controller;

import org.kodluyoruz.mybank.account.savingsaccount.dto.SavingsAccountDto;
import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.kodluyoruz.mybank.account.savingsaccount.service.SavingsAccountService;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.service.CustomerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/savings")
public class SavingsAccountController {
    private final SavingsAccountService savingsAccountService;
    private final CustomerService customerService;

    public SavingsAccountController(SavingsAccountService savingsAccountService, CustomerService customerService) {
        this.savingsAccountService = savingsAccountService;
        this.customerService = customerService;
    }
    @PostMapping("/{customerID}/account")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto create(@PathVariable("customerID") long customerID, @RequestBody SavingsAccountDto savingsAccountDto){
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        savingsAccountDto.setCustomer(customerDto.toCustomer());
        return savingsAccountService.create(savingsAccountDto.toSavingsAccount()).toSavingsAccountDto();
    }
    @GetMapping("/{accountIBAN}")
    public SavingsAccountDto get(@PathVariable("accountIBAN") int accountIBAN){
        return savingsAccountService.get(accountIBAN).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Savings Account is not fount")).toSavingsAccountDto();
    }
    @GetMapping(value = "/accounts",params = {"page","size"})
    public List<SavingsAccountDto> getAllSavingsAccount(@Min(value = 0) @RequestParam("page") int page, @Min(value = 1) @RequestParam("size") int size){
        return savingsAccountService.savingsAccounts(PageRequest.of(page, size)).stream()
                .map(SavingsAccount::toSavingsAccountDto)
                .collect(Collectors.toList());
    }
}
