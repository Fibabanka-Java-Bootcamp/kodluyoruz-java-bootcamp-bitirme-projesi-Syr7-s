package org.kodluyoruz.mybank.account.demanddepositaccount.concrete;

import org.apache.log4j.Logger;
import org.kodluyoruz.mybank.account.demanddepositaccount.abstrct.DemandDepositAccountService;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotDeletedException;
import org.kodluyoruz.mybank.exchange.concrete.Exchange;
import org.kodluyoruz.mybank.shopping.abstrct.ShoppingService;
import org.kodluyoruz.mybank.shopping.concrete.Shopping;
import org.kodluyoruz.mybank.shopping.concrete.ShoppingDto;
import org.kodluyoruz.mybank.utilities.enums.messages.Messages;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/deposit")
public class DemandDepositAccountController {
    private static final Logger log = Logger.getLogger(DemandDepositAccountController.class);
    private final DemandDepositAccountService<DemandDepositAccount> demandDepositAccountService;
    private final ShoppingService<Shopping> shoppingService;

    public DemandDepositAccountController(DemandDepositAccountService<DemandDepositAccount> demandDepositAccountService, ShoppingService<Shopping> shoppingService) {
        this.demandDepositAccountService = demandDepositAccountService;

        this.shoppingService = shoppingService;
    }

    @PostMapping("/{customerTC}/account/{bankCardAccountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto create(@PathVariable("customerTC") long customerTC, @PathVariable("bankCardAccountNumber") long bankCardAccountNumber, @RequestBody DemandDepositAccountDto demandDepositAccountDto) {
        log.info("Account was created.");
        return demandDepositAccountService.create(customerTC, bankCardAccountNumber, demandDepositAccountDto).toDemandDepositAccountDto();
    }

    @GetMapping("/{accountNumber}")
    public DemandDepositAccountDto getDemandDepositAccount(@PathVariable("accountNumber") long accountNumber) {
        log.info(accountNumber + " account was got.");
        return demandDepositAccountService.get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.ACCOUNT_COULD_NOT_FOUND.message)).toDemandDepositAccountDto();
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
                                                     @Min(value = 0) @RequestParam("depositMoney") int depositMoney) {
        log.info("Deposit Money process was did.");
        return demandDepositAccountService.depositMoney(bankCardAccountNumber, password, accountNumber, depositMoney).toDemandDepositAccountDto();
    }

    @PutMapping("/{bankCardAccountNumber}/withDrawMoney/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getUpdateDepositWithDrawMoney(@PathVariable("bankCardAccountNumber") long bankCardAccountNumber,
                                                                 @PathVariable("accountNumber") long accountNumber,
                                                                 @RequestParam("password") int password,
                                                                 @Min(value = 0) @RequestParam("withDrawMoney") int withDrawMoney) {
        log.info("With draw money process was did.");
        return demandDepositAccountService.withDrawMoney(bankCardAccountNumber, password, accountNumber, withDrawMoney).toDemandDepositAccountDto();
    }

    @PutMapping("/{depositAccountIBAN}/transfer/{savingsAccountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getMoneyTransfer(@PathVariable("depositAccountIBAN") String depositAccountIBAN,
                                                    @PathVariable("savingsAccountIBAN") String savingsAccountIBAN,
                                                    @Min(value = 0) @RequestParam("transferMoney") int transferMoney) {
        log.info("Money transfer process was did.");
        return demandDepositAccountService.moneyTransferBetweenDifferentAccounts(depositAccountIBAN, savingsAccountIBAN, transferMoney).toDemandDepositAccountDto();
    }

    @PutMapping("/{fromAccountIBAN}/betweenAccountMoneyTransfer/{toAccountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getBetweenAccountTransferMoney(@PathVariable("fromAccountIBAN") String fromAccountIBAN,
                                                                  @PathVariable("toAccountIBAN") String toAccountIBAN,
                                                                  @Min(value = 0) @RequestParam("transferMoney") int transferMoney) {

        return demandDepositAccountService.moneyTransferBetweenAccounts(fromAccountIBAN, toAccountIBAN, transferMoney).toDemandDepositAccountDto();
    }

    @PutMapping("/{accountNumber}/payDebt/{creditCardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto payDebtWithDemandDeposit(@PathVariable("accountNumber") long accountNumber,
                                                            @PathVariable("creditCardNumber") long creditCardNumber,
                                                            @Min(value = 0) @RequestParam("creditCardDebt") int creditCardDebt,
                                                            @Min(value = 0) @RequestParam("minimumPaymentAmount") int minimumPaymentAmount) {

        return demandDepositAccountService.payDebtWithDemandDeposit(accountNumber, creditCardNumber, creditCardDebt, minimumPaymentAmount).toDemandDepositAccountDto();
    }

    @DeleteMapping("/{accountNumber}/process")
    public String demandDepositAccountDelete(@PathVariable("accountNumber") long accountNumber) {
        try {
            log.info(accountNumber + " number will deleted.");
            return demandDepositAccountService.delete(accountNumber);
        } catch (DemandDepositAccountNotDeletedException exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (RuntimeException exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.SERVER_ERROR.message);
        }
    }


    @PutMapping("/{bankCardAccountNumber}/currency/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto currencyProcess(@PathVariable("bankCardAccountNumber") long bankCardAccountNumber,
                                                   @PathVariable("accountNumber") long accountNumber,
                                                   @Min(value = 0) @RequestParam("money") int money,
                                                   @RequestParam("password") int password,
                                                   @RequestBody ShoppingDto shoppingDto) {
        Thread withDrawMoney = new Thread(() -> withDrawMoneyProcess(accountNumber, money));
        Thread shoppingMoneyThread = new Thread(() -> shoppingProcess(bankCardAccountNumber, accountNumber, password, shoppingDto));
        withDrawMoney.start();
        shoppingMoneyThread.start();
        try {
            withDrawMoney.join();
            shoppingMoneyThread.join();
        } catch (Exception exception) {
            log.error(Messages.Error.SERVER_ERROR.message);
        }
        return demandDepositAccountService.get(accountNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.ACCOUNT_COULD_NOT_FOUND.message)).toDemandDepositAccountDto();

    }

    private void withDrawMoneyProcess(long accountNumber, int money) {
        try {
            int time = 1 + new Random().nextInt(9);
            log.info("Time : " + (time * 100));
            Thread.sleep(time);
            log.info("Money : " + money + " Balance Thread 1 : " + demandDepositAccountService.updateBalanceFromAccount(accountNumber, money).getDemandDepositAccountBalance());
        } catch (Exception exception) {
            log.error("With draw money : " + exception.getMessage());
        }
    }

    private void shoppingProcess(long bankCardAccountNumber, long accountNumber, int password, ShoppingDto shoppingDto) {
        try {
            DemandDepositAccount demandDepositAccount = demandDepositAccountService.get(accountNumber).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found"));
            if (demandDepositAccount.getBankCard().getBankCardAccountNumber() == bankCardAccountNumber && demandDepositAccount.getBankCard().getBankCardPassword() == password) {
                double moneyProcess = Exchange.convertProcess(shoppingDto.getCurrency(), demandDepositAccount.getDemandDepositAccountCurrency(), shoppingDto.getProductPrice());
                log.info("Shopping Money : " + moneyProcess + " Balance Thread 2 : " + demandDepositAccountService.updateBalanceFromAccount(accountNumber, (int) moneyProcess).getDemandDepositAccountBalance());
                shoppingService.create(shoppingDto.toShopping());
            } else {
                log.error("Shopping Money Thread " + Messages.Error.ACCOUNT_NUMBER_AND_PASSWORD_COULD_NOT_MATCHED.message);
            }
        } catch (Exception exception) {
            log.error("Shopping Money Thread " + exception.getMessage());
        }
    }


    @PutMapping("/finalSituation/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto currencyProcess(@PathVariable("accountNumber") long accountNumber,
                                                   @Min(value = 0) @RequestParam("money") int money,
                                                   @Min(value = 0) @RequestParam("shoppingMoney") int shoppingMoney) {

        return demandDepositAccountService.withDrawMoneyAndShopping(accountNumber, money, shoppingMoney).toDemandDepositAccountDto();
    }

}
