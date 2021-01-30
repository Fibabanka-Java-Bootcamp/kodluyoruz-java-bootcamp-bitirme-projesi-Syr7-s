package org.kodluyoruz.mybank.account.savingsaccount.controller;

import org.kodluyoruz.mybank.account.savingsaccount.dto.SavingsAccountDto;
import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.kodluyoruz.mybank.account.savingsaccount.exception.SavingsAccountNotEnoughMoneyException;
import org.kodluyoruz.mybank.account.savingsaccount.service.SavingsAccountService;
import org.kodluyoruz.mybank.bankcard.dto.BankCardDto;
import org.kodluyoruz.mybank.bankcard.exception.BankCardNotMatchException;
import org.kodluyoruz.mybank.bankcard.service.BankCardService;
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
    private final BankCardService bankCardService;

    public SavingsAccountController(SavingsAccountService savingsAccountService, CustomerService customerService, BankCardService bankCardService) {
        this.savingsAccountService = savingsAccountService;
        this.customerService = customerService;
        this.bankCardService = bankCardService;
    }

    @PostMapping("/{customerID}/account/{bankcardNO}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto create(@PathVariable("customerID") long customerID, @PathVariable("bankcardNO") long bankCardNO, @RequestBody SavingsAccountDto savingsAccountDto) {
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        savingsAccountDto.setCustomer(customerDto.toCustomer());
        BankCardDto bankCardDto = bankCardService.findBankCard(bankCardNO).toBankCardDto();
        savingsAccountDto.setBankCard(bankCardDto.toBankCard());
        return savingsAccountService.create(savingsAccountDto.toSavingsAccount()).toSavingsAccountDto();
    }

    @GetMapping("/{accountIBAN}")
    public SavingsAccountDto get(@PathVariable("accountIBAN") int accountIBAN) {
        return savingsAccountService.get(accountIBAN).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Savings Account is not found")).toSavingsAccountDto();
    }

    @GetMapping(value = "/accounts", params = {"page", "size"})
    public List<SavingsAccountDto> getAllSavingsAccount(@Min(value = 0) @RequestParam("page") int page, @Min(value = 1) @RequestParam("size") int size) {
        return savingsAccountService.savingsAccounts(PageRequest.of(page, size)).stream()
                .map(SavingsAccount::toSavingsAccountDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{bankCardNo}/deposit/{accountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto getUpdateSavings(@PathVariable("bankCardNo") long bankCardNo,
                                              @PathVariable("accountIBAN") int accountIBAN, @RequestParam("depositMoney") int depositMoney) {
        SavingsAccountDto savingsAccountDto = savingsAccountService.get(accountIBAN).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toSavingsAccountDto();
        long cardNo = savingsAccountDto.getBankCard().getBankCardNO();
        if (cardNo == bankCardNo) {
            int balance = savingsAccountDto.getSavingsAccountBalance();
            savingsAccountDto.setSavingsAccountBalance(balance + depositMoney);
            return savingsAccountService.updateBalance(savingsAccountDto.toSavingsAccount()).toSavingsAccountDto();
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

    @PutMapping("/{bankCardNo}/withDrawMoney/{accountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto getUpdateSavingsWithDrawMoney(@PathVariable("bankCardNo") long bankCardNo,
                                                           @PathVariable("accountIBAN") int accountIBAN, @RequestParam("withDrawMoney") int withDrawMoney) {
        SavingsAccountDto savingsAccountDto = savingsAccountService.get(accountIBAN).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toSavingsAccountDto();
        long cardNo = savingsAccountDto.getBankCard().getBankCardNO();
        if (cardNo == bankCardNo){
            int balance = savingsAccountDto.getSavingsAccountBalance();
            if (balance<withDrawMoney){
                throw new SavingsAccountNotEnoughMoneyException("Not enough money in your account.");
            }else{
                savingsAccountDto.setSavingsAccountBalance(balance-withDrawMoney);
                return savingsAccountService.updateBalance(savingsAccountDto.toSavingsAccount()).toSavingsAccountDto();
            }
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

}
