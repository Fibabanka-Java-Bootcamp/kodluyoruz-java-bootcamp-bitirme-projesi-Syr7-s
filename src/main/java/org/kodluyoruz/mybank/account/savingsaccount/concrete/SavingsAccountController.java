package org.kodluyoruz.mybank.account.savingsaccount.concrete;

import org.apache.log4j.Logger;
import org.kodluyoruz.mybank.account.savingsaccount.abtrct.SavingsAccountService;
import org.kodluyoruz.mybank.account.savingsaccount.exception.SavingAccountNotDeletedException;
import org.kodluyoruz.mybank.utilities.enums.messages.Messages;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/savings")
public class SavingsAccountController {
    private final SavingsAccountService<SavingsAccount> savingsAccountService;
    private static final Logger log = Logger.getLogger(SavingsAccountController.class);

    public SavingsAccountController(SavingsAccountService<SavingsAccount> savingsAccountService) {
        this.savingsAccountService = savingsAccountService;
    }

    @PostMapping("/{customerTC}/account/{bankCardAccountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto create(@PathVariable("customerTC") long customerTC, @PathVariable("bankCardAccountNumber") long bankCardAccountNumber, @RequestBody SavingsAccountDto savingsAccountDto) {
        log.info("Savings account will create.");
        return savingsAccountService.create(customerTC, bankCardAccountNumber, savingsAccountDto).toSavingsAccountDto();
    }

    @GetMapping("/{accountNumber}")
    public SavingsAccountDto get(@PathVariable("accountNumber") long accountNumber) {
        log.info(accountNumber + " will get");
        return savingsAccountService.get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.ACCOUNT_COULD_NOT_FOUND.message)).toSavingsAccountDto();
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
                                              @PathVariable("accountNumber") long accountNumber,
                                              @RequestParam("password") int password,
                                              @Min(value = 0) @RequestParam("depositMoney") int depositMoney) {
        log.info("Deposit Money process will do.");
        return savingsAccountService.depositMoney(bankCardAccountNumber, password, accountNumber, depositMoney).toSavingsAccountDto();
    }

    @PutMapping("/{bankCardAccountNumber}/withDrawMoney/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto getUpdateSavingsWithDrawMoney(@PathVariable("bankCardAccountNumber") long bankCardAccountNumber,
                                                           @PathVariable("accountNumber") long accountNumber,
                                                           @RequestParam("password") int password,
                                                           @Min(value = 0) @RequestParam("withDrawMoney") int withDrawMoney) {
        log.info("With draw money process will do.");
        return savingsAccountService.withDrawMoney(bankCardAccountNumber, password, accountNumber, withDrawMoney).toSavingsAccountDto();
    }

    @PutMapping("/{accountNumber}/payDebt/{creditCardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto payDebtWithSaving(@PathVariable("accountNumber") long accountNumber,
                                               @PathVariable("creditCardNumber") long creditCardNumber,
                                               @Min(value = 0) @RequestParam("creditCardDebt") int creditCardDebt,
                                               @Min(value = 0) @RequestParam("minimumPaymentAmount") int minimumPaymentAmount) {
        log.info("Debt will payment with savings account.");
        return savingsAccountService.payDebtWithAccount(accountNumber, creditCardNumber, creditCardDebt, minimumPaymentAmount).toSavingsAccountDto();
    }

    @PutMapping("/{accountNumber}/savingsProcess")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto computeSavings(@PathVariable("accountNumber") long accountNumber,
                                            @Min(value = 0) @RequestParam("termTime") int termTime,
                                            @Min(value = 0) @RequestParam("interestRate") double interestRate,
                                            @Min(value = 0) @RequestParam("withHoldingValue") double withHoldingValue) {
        return savingsAccountService.computeSavings(accountNumber, termTime, interestRate, withHoldingValue).toSavingsAccountDto();
    }

    @DeleteMapping("/{accountNumber}/process")
    public String savingAccountDelete(@PathVariable("accountNumber") long accountNumber) {
        try {
            log.info(accountNumber + " will delete.");
            return savingsAccountService.delete(accountNumber);
        } catch (SavingAccountNotDeletedException exception) {
            log.error(Messages.Error.ACCOUNT_COULD_NOT_DELETED_BECAUSE_HAVE_MONEY_IN_YOUR_ACCOUNT.message);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Messages.Error.ACCOUNT_COULD_NOT_DELETED_BECAUSE_HAVE_MONEY_IN_YOUR_ACCOUNT.message);
        } catch (RuntimeException exception) {
            log.error(Messages.Error.SERVER_ERROR.message);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.SERVER_ERROR.message);
        }
    }
}
