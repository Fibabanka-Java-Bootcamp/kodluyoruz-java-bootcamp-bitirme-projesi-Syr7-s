package org.kodluyoruz.mybank.account.demanddepositaccount.controller;

import org.kodluyoruz.mybank.account.demanddepositaccount.dto.DemandDepositAccountDto;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotEnoughMoneyException;
import org.kodluyoruz.mybank.account.demanddepositaccount.service.DemandDepositAccountService;
import org.kodluyoruz.mybank.account.savingsaccount.dto.SavingsAccountDto;
import org.kodluyoruz.mybank.account.savingsaccount.service.SavingsAccountService;
import org.kodluyoruz.mybank.bankcard.dto.BankCardDto;
import org.kodluyoruz.mybank.bankcard.exception.BankCardNotMatchException;
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
    private final SavingsAccountService savingsAccountService;

    public DemandDepositAccountController(DemandDepositAccountService demandDepositAccountService, CustomerService customerService, BankCardService bankCardService, SavingsAccountService savingsAccountService) {
        this.demandDepositAccountService = demandDepositAccountService;
        this.customerService = customerService;
        this.bankCardService = bankCardService;
        this.savingsAccountService = savingsAccountService;
    }

    @PostMapping("/{customerID}/account/{bankcardNO}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto create(@PathVariable("customerID") long customerID, @PathVariable("bankcardNO") long bankCardNO, @RequestBody DemandDepositAccountDto demandDepositAccountDto) {
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

    @PutMapping("/{bankCardNo}/deposit/{accountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getUpdatedDeposit(@PathVariable("bankCardNo") long bankCardNo,
                                                     @PathVariable("accountIBAN") int accountIBAN, @RequestParam("depositMoney") int depositMoney) {
        DemandDepositAccountDto demandDepositAccountDto = demandDepositAccountService.get(accountIBAN).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toDemandDepositAccountDto();
        long cardNo = demandDepositAccountDto.getBankCard().getBankCardNO();
        if (cardNo == bankCardNo) {
            int balance = demandDepositAccountDto.getDemandDepositAccountBalance();
            demandDepositAccountDto.setDemandDepositAccountBalance(balance + depositMoney);
            return demandDepositAccountService.update(demandDepositAccountDto.toDemandDepositAccount()).toDemandDepositAccountDto();
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

    @PutMapping("/{bankCardNo}/withDrawMoney/{accountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getUpdateDepositWithDrawMoney(@PathVariable("bankCardNo") long bankCardNo,
                                                                 @PathVariable("accountIBAN") int accountIBAN, @RequestParam("withDrawMoney") int withDrawMoney) {
        DemandDepositAccountDto demandDepositAccountDto = demandDepositAccountService.get(accountIBAN).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toDemandDepositAccountDto();
        long cardNo = demandDepositAccountDto.getBankCard().getBankCardNO();
        if (cardNo == bankCardNo) {
            int balance = demandDepositAccountDto.getDemandDepositAccountBalance();
            if (balance < withDrawMoney) {
                throw new DemandDepositAccountNotEnoughMoneyException("Not enough money in your account");
            } else {
                demandDepositAccountDto.setDemandDepositAccountBalance(balance - withDrawMoney);
                return demandDepositAccountService.update(demandDepositAccountDto.toDemandDepositAccount()).toDemandDepositAccountDto();
            }
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

    @PutMapping("/{depositAccountIBAN}/transfer/{savingsAccountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getMoneyTransfer(@PathVariable("depositAccountIBAN") int depositAccountIBAN,
                                                    @PathVariable("savingsAccountIBAN") int savingsAccountIBAN,
                                                    @RequestParam("transferMoney") int transferMoney) {
        DemandDepositAccountDto demandDepositAccountDto = demandDepositAccountService.get(depositAccountIBAN).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "DepositAccount is not found")).toDemandDepositAccountDto();
        SavingsAccountDto savingsAccountDto = savingsAccountService.get(savingsAccountIBAN).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "SavingsAccount is not found")).toSavingsAccountDto();
        int demandDepositMoney = demandDepositAccountDto.getDemandDepositAccountBalance();
        int savingsMoney = savingsAccountDto.getSavingsAccountBalance();
        if (demandDepositMoney - transferMoney < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough money in your demandDepositAccount");
        } else {
            demandDepositAccountDto.setDemandDepositAccountBalance(demandDepositMoney - transferMoney);
            savingsAccountDto.setSavingsAccountBalance(savingsMoney + transferMoney);
            savingsAccountService.updateBalance(savingsAccountDto.toSavingsAccount()).toSavingsAccountDto();
            return demandDepositAccountService.update(demandDepositAccountDto.toDemandDepositAccount()).toDemandDepositAccountDto();

        }
    }

    @PutMapping("/{fromAccountIBAN}/betweenAccountMoneyTransfer/{toAccountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getBetweenAccountTransferMoney(@PathVariable("fromAccountIBAN") int fromAccountIBAN,
                                                                  @PathVariable("toAccountIBAN") int toAccountIBAN,
                                                                  @RequestParam("transferMoney") int transferMoney) {
        if (fromAccountIBAN != toAccountIBAN) {
            DemandDepositAccountDto fromAccount = demandDepositAccountService.get(fromAccountIBAN).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "DepositAccount is not found")).toDemandDepositAccountDto();
            DemandDepositAccountDto toAccount = demandDepositAccountService.get(toAccountIBAN).orElseThrow(()
                    -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deposit Account is not found(toAccountIBAN)")).toDemandDepositAccountDto();
            int fromMoney = fromAccount.getDemandDepositAccountBalance();
            int toMoney = toAccount.getDemandDepositAccountBalance();
            if (fromMoney - transferMoney < 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough money in your demandDepositAccount");
            } else {
                fromAccount.setDemandDepositAccountBalance(fromMoney - transferMoney);
                toAccount.setDemandDepositAccountBalance(toMoney + transferMoney);
                demandDepositAccountService.update(toAccount.toDemandDepositAccount()).toDemandDepositAccountDto();
                return demandDepositAccountService.update(fromAccount.toDemandDepositAccount()).toDemandDepositAccountDto();
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Same account.");
        }
    }
}
