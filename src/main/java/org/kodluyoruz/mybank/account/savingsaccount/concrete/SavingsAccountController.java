package org.kodluyoruz.mybank.account.savingsaccount.concrete;

import org.kodluyoruz.mybank.account.savingsaccount.abtrct.SavingsAccountService;
import org.kodluyoruz.mybank.account.savingsaccount.exception.SavingAccountNotDeletedException;
import org.kodluyoruz.mybank.utilities.messages.ErrorMessages;
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
    private final SavingsAccountService<SavingsAccount> savingsAccountService;

    public SavingsAccountController(SavingsAccountService<SavingsAccount> savingsAccountService) {
        this.savingsAccountService = savingsAccountService;
    }

    @PostMapping("/{customerTC}/account/{bankCardAccountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto create(@PathVariable("customerTC") long customerTC, @PathVariable("bankCardAccountNumber") long bankCardAccountNumber, @RequestBody SavingsAccountDto savingsAccountDto) {

        return savingsAccountService.create(customerTC, bankCardAccountNumber, savingsAccountDto).toSavingsAccountDto();
    }

    @GetMapping("/{accountNumber}")
    public SavingsAccountDto get(@PathVariable("accountNumber") long accountNumber) {
        return savingsAccountService.get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.ACCOUNT_COULD_NOT_FOUND)).toSavingsAccountDto();
    }

    @GetMapping(value = "/accounts", params = {"page", "size"})
    public List<SavingsAccountDto> getAllSavingsAccount(@Min(value = 0) @RequestParam("page") int page, @Min(value = 1) @RequestParam("size") int size) {
        return savingsAccountService.accounts(PageRequest.of(page, size)).stream()
                .map(SavingsAccount::toSavingsAccountDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{bankCardAccountNumber}/deposit/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto getUpdateSavings(@PathVariable("bankCardAccountNumber") long bankCardAccountNumber,
                                              @PathVariable("accountNumber") long accountNumber, @RequestParam("depositMoney") int depositMoney) {

        return savingsAccountService.depositMoney(bankCardAccountNumber, accountNumber, depositMoney).toSavingsAccountDto();
    }

    @PutMapping("/{bankCardAccountNumber}/withDrawMoney/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto getUpdateSavingsWithDrawMoney(@PathVariable("bankCardAccountNumber") long bankCardAccountNumber,
                                                           @PathVariable("accountNumber") long accountNumber, @RequestParam("withDrawMoney") int withDrawMoney) {

        return savingsAccountService.withDrawMoney(bankCardAccountNumber, accountNumber, withDrawMoney).toSavingsAccountDto();
    }

    @PutMapping("/{accountNumber}/payDebt/{creditCardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto payDebtWithSaving(@PathVariable("accountNumber") long accountNumber,
                                               @PathVariable("creditCardNumber") long creditCardNumber,
                                               @RequestParam("creditCardDebt") int creditCardDebt,
                                               @RequestParam("minimumPaymentAmount") int minimumPaymentAmount) {

        return savingsAccountService.payDebtWithAccount(accountNumber, creditCardNumber, creditCardDebt, minimumPaymentAmount).toSavingsAccountDto();
    }

    @DeleteMapping("/{accountNumber}/process")
    public void savingAccountDelete(@PathVariable("accountNumber") long accountNumber) {
        try {
            savingsAccountService.delete(accountNumber);
        } catch (SavingAccountNotDeletedException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.SERVER_ERROR);
        }
    }
}
