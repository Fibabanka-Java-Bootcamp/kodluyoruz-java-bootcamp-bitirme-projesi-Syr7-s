package org.kodluyoruz.mybank.account.demanddepositaccount.concrete;

import org.kodluyoruz.mybank.account.demanddepositaccount.abstrct.DemandDepositAccountService;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotDeletedException;
import org.kodluyoruz.mybank.utilities.messages.ErrorMessages;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/deposit")
public class DemandDepositAccountController {

    private final DemandDepositAccountService<DemandDepositAccount> demandDepositAccountService;

    public DemandDepositAccountController(DemandDepositAccountService<DemandDepositAccount> demandDepositAccountService) {
        this.demandDepositAccountService = demandDepositAccountService;
    }

    @PostMapping("/{customerTC}/account/{bankCardAccountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto create(@PathVariable("customerTC") long customerTC, @PathVariable("bankCardAccountNumber") long bankCardAccountNumber, @RequestBody DemandDepositAccountDto demandDepositAccountDto) {

        return demandDepositAccountService.create(customerTC, bankCardAccountNumber, demandDepositAccountDto).toDemandDepositAccountDto();
    }

    @GetMapping("/{accountNumber}")
    public DemandDepositAccountDto getDemandDepositAccount(@PathVariable("accountNumber") long accountNumber) {
        return demandDepositAccountService.get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.ACCOUNT_COULD_NOT_FOUND)).toDemandDepositAccountDto();
    }

    @GetMapping(value = "/accounts", params = {"page", "size"})
    public List<DemandDepositAccountDto> getAccounts(@Min(value = 0) @RequestParam("page") int page, @Min(value = 1) @RequestParam("size") int size) {
        return demandDepositAccountService.getDemandDepositAccounts(PageRequest.of(page, size)).stream()
                .map(DemandDepositAccount::toDemandDepositAccountDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{bankCardAccountNumber}/deposit/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getUpdatedDeposit(@PathVariable("bankCardAccountNumber") long bankCardAccountNumber,
                                                     @PathVariable("accountNumber") long accountNumber,
                                                     @RequestParam("password") int password,
                                                     @RequestParam("depositMoney") int depositMoney) {

        return demandDepositAccountService.depositMoney(bankCardAccountNumber, password, accountNumber, depositMoney).toDemandDepositAccountDto();
    }

    @PutMapping("/{bankCardAccountNumber}/withDrawMoney/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getUpdateDepositWithDrawMoney(@PathVariable("bankCardAccountNumber") long bankCardAccountNumber,
                                                                 @PathVariable("accountNumber") long accountNumber,
                                                                 @RequestParam("password") int password,
                                                                 @RequestParam("withDrawMoney") int withDrawMoney) {

        return demandDepositAccountService.withDrawMoney(bankCardAccountNumber, password, accountNumber, withDrawMoney).toDemandDepositAccountDto();
    }

    @PutMapping("/{bankCardAccountNO}/bankCard/{depositAccountIBAN}/transfer/{savingsAccountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getMoneyTransfer(@PathVariable("depositAccountIBAN") String depositAccountIBAN,
                                                    @PathVariable("savingsAccountIBAN") String savingsAccountIBAN,
                                                    @RequestParam("bankCardAccountNO") long bankCardAccountNumber,
                                                    @RequestParam("password") int password,
                                                    @RequestParam("transferMoney") int transferMoney) {

        return demandDepositAccountService.moneyTransferBetweenDifferentAccounts(bankCardAccountNumber, password, depositAccountIBAN, savingsAccountIBAN, transferMoney).toDemandDepositAccountDto();
    }

    @PutMapping("/{bankCardAccountNO}/bankCard/{fromAccountIBAN}/betweenAccountMoneyTransfer/{toAccountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getBetweenAccountTransferMoney(@PathVariable("bankCardAccountNO") long bankCardAccountNumber,
                                                                  @PathVariable("fromAccountIBAN") String fromAccountIBAN,
                                                                  @PathVariable("toAccountIBAN") String toAccountIBAN,
                                                                  @RequestParam("password") int password,
                                                                  @RequestParam("transferMoney") int transferMoney) {

        return demandDepositAccountService.moneyTransferBetweenAccounts(bankCardAccountNumber, password, fromAccountIBAN, toAccountIBAN, transferMoney).toDemandDepositAccountDto();
    }

    @PutMapping("/{bankCardAccountNO}/bankCard/{accountNumber}/payDebt/{creditCardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto payDebtWithDemandDeposit(@PathVariable("bankCardAccountNO") long bankCardAccountNumber,
                                                            @PathVariable("accountNumber") long accountNumber,
                                                            @PathVariable("creditCardNumber") long creditCardNumber,
                                                            @RequestParam("password") int password,
                                                            @RequestParam("creditCardDebt") int creditCardDebt,
                                                            @RequestParam("minimumPaymentAmount") int minimumPaymentAmount) {

        return demandDepositAccountService.payDebtWithDemandDeposit(bankCardAccountNumber, password, accountNumber, creditCardNumber, creditCardDebt, minimumPaymentAmount).toDemandDepositAccountDto();
    }

    @DeleteMapping("/{accountNumber}/process")
    public void demandDepositAccountDelete(@PathVariable("accountNumber") long accountNumber) {
        try {
            demandDepositAccountService.delete(accountNumber);
        } catch (DemandDepositAccountNotDeletedException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (RuntimeException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.SERVER_ERROR);
        }
    }
}
