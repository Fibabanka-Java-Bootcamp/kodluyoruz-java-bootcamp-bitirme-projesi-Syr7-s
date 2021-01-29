package org.kodluyoruz.mybank.account.demanddepositaccount.controller;

import org.kodluyoruz.mybank.account.demanddepositaccount.dto.DemandDepositAccountDto;
import org.kodluyoruz.mybank.account.demanddepositaccount.service.DemandDepositAccountService;
import org.kodluyoruz.mybank.bankcard.dto.BankCardDto;
import org.kodluyoruz.mybank.bankcard.service.BankCardService;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/deposit")
public class DemandDepositAccountController {
    private final DemandDepositAccountService demandDepositAccountService;
    private final CustomerService customerService;
    private final BankCardService bankCardService;

    public DemandDepositAccountController(DemandDepositAccountService demandDepositAccountService, CustomerService customerService, BankCardService bankCardService) {
        this.demandDepositAccountService = demandDepositAccountService;
        this.customerService = customerService;
        this.bankCardService = bankCardService;
    }

    @PostMapping("/{customerID}/account/{bankcardNO}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto create(@PathVariable("customerID") long customerID,@PathVariable("bankcardNO") long bankCardNO, @RequestBody DemandDepositAccountDto demandDepositAccountDto) {
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        demandDepositAccountDto.setCustomer(customerDto.toCustomer());
        BankCardDto bankCardDto = bankCardService.findBankCard(bankCardNO).toBankCardDto();
        demandDepositAccountDto.setBankCard(bankCardDto.toBankCard());
        return demandDepositAccountService.create(demandDepositAccountDto.toDemandDepositAccount()).toDemandDepositAccountDto();


    }

    @GetMapping("/{accountIBAN}")
    public DemandDepositAccountDto getDemandDepositAccount(@PathVariable("accountIBAN") int accountIBAN) {
        return demandDepositAccountService.get(accountIBAN).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toDemandDepositAccountDto();
    }

}
