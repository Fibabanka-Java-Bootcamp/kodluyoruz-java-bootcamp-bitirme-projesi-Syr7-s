package org.kodluyoruz.mybank.account.demanddepositaccount.controller;

import jdk.management.resource.ResourceRequestDeniedException;
import org.kodluyoruz.mybank.account.demanddepositaccount.dto.DemandDepositAccountDto;
import org.kodluyoruz.mybank.account.demanddepositaccount.entity.DemandDepositAccount;
import org.kodluyoruz.mybank.account.demanddepositaccount.service.DemandDepositAccountService;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/deposit")
public class DemandDepositAccountController {
    private final DemandDepositAccountService demandDepositAccountService;
    private final CustomerService customerService;

    public DemandDepositAccountController(DemandDepositAccountService demandDepositAccountService, CustomerService customerService) {
        this.demandDepositAccountService = demandDepositAccountService;
        this.customerService = customerService;
    }

    @PostMapping("/{customerID}/account")
    public DemandDepositAccountDto create(@PathVariable("customerID") long customerID, @RequestBody DemandDepositAccountDto demandDepositAccountDto) {
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        demandDepositAccountDto.setCustomer(customerDto.toCustomer());
        return demandDepositAccountService.create(demandDepositAccountDto.toDemandDepositAccount()).toDemandDepositAccountDto();
    }

   /* @GetMapping("/customerID")
    public DemandDepositAccountDto getDemandDepositAccount(@PathVariable("customerID") int accountIBAN) {
        return demandDepositAccountService.get(accountIBAN).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toDemandDepositAccountDto();
    }*/
   @GetMapping("/{customerID}")
   public DemandDepositAccountDto getDemandDepositAccount(@PathVariable("customerID") int customerID) {
       try{
           return demandDepositAccountService.getByCustomerID(customerID).toDemandDepositAccountDto();
       }catch (Exception exception){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND,"DemandDepositAccount is not found with customerID");
       }
   }
}
