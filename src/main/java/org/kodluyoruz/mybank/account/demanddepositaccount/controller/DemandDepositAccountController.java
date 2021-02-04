package org.kodluyoruz.mybank.account.demanddepositaccount.controller;

import org.kodluyoruz.mybank.account.demanddepositaccount.dto.DemandDepositAccountDto;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotEnoughMoneyException;
import org.kodluyoruz.mybank.account.demanddepositaccount.service.DemandDepositAccountService;
import org.kodluyoruz.mybank.account.savingsaccount.dto.SavingsAccountDto;
import org.kodluyoruz.mybank.account.savingsaccount.service.SavingsAccountService;
import org.kodluyoruz.mybank.card.bankcard.dto.BankCardDto;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotMatchException;
import org.kodluyoruz.mybank.card.bankcard.service.BankCardService;
import org.kodluyoruz.mybank.card.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.service.CreditCardService;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.service.CustomerService;
import org.kodluyoruz.mybank.exchange.Exchange;
import org.kodluyoruz.mybank.exchange.ExchangeDto;
import org.kodluyoruz.mybank.extractofaccount.entity.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.service.ExtractOfAccountService;
import org.kodluyoruz.mybank.generate.accountgenerate.AccountGenerate;
import org.kodluyoruz.mybank.generate.ibangenerate.IbanGenerate;
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
    private final CreditCardService creditCardService;
    private final ExtractOfAccountService extractOfAccountService;

    public DemandDepositAccountController(DemandDepositAccountService demandDepositAccountService, CustomerService customerService, BankCardService bankCardService, SavingsAccountService savingsAccountService, CreditCardService creditCardService, ExtractOfAccountService extractOfAccountService) {
        this.demandDepositAccountService = demandDepositAccountService;
        this.customerService = customerService;
        this.bankCardService = bankCardService;
        this.savingsAccountService = savingsAccountService;
        this.creditCardService = creditCardService;
        this.extractOfAccountService = extractOfAccountService;
    }

    @PostMapping("/{customerID}/account/{bankCardAccountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto create(@PathVariable("customerID") long customerID, @PathVariable("bankCardAccountNumber") long bankCardAccountNumber, @RequestBody DemandDepositAccountDto demandDepositAccountDto) {
        String accountNumber = AccountGenerate.accountGenerate.get();
        demandDepositAccountDto.setDemandDepositAccountNumber(Long.parseLong(accountNumber));
        demandDepositAccountDto.setDemandDepositAccountIBAN(IbanGenerate.ibanGenerate.apply(accountNumber));
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        demandDepositAccountDto.setCustomer(customerDto.toCustomer());
        BankCardDto bankCardDto = bankCardService.findBankCard(bankCardAccountNumber).toBankCardDto();
        demandDepositAccountDto.setBankCard(bankCardDto.toBankCard());
        return demandDepositAccountService.create(demandDepositAccountDto.toDemandDepositAccount()).toDemandDepositAccountDto();
    }

    @GetMapping("/{accountNumber}")
    public DemandDepositAccountDto getDemandDepositAccount(@PathVariable("accountNumber") long accountNumber) {
        return demandDepositAccountService.get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found.")).toDemandDepositAccountDto();
    }

    @PutMapping("/{bankCardAccountNumber}/deposit/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getUpdatedDeposit(@PathVariable("bankCardAccountNumber") long bankCardAccountNumber,
                                                     @PathVariable("accountNumber") long accountNumber, @RequestParam("depositMoney") int depositMoney) {
        DemandDepositAccountDto demandDepositAccountDto = demandDepositAccountService.get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toDemandDepositAccountDto();
        long cardAccountNumber = demandDepositAccountDto.getBankCard().getBankCardAccountNumber();
        if (cardAccountNumber == bankCardAccountNumber) {
            int balance = demandDepositAccountDto.getDemandDepositAccountBalance();
            demandDepositAccountDto.setDemandDepositAccountBalance(balance + depositMoney);
            return demandDepositAccountService.update(demandDepositAccountDto.toDemandDepositAccount()).toDemandDepositAccountDto();
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

    @PutMapping("/{bankCardAccountNumber}/withDrawMoney/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getUpdateDepositWithDrawMoney(@PathVariable("bankCardAccountNumber") long bankCardAccountNumber,
                                                                 @PathVariable("accountNumber") long accountNumber, @RequestParam("withDrawMoney") int withDrawMoney) {
        DemandDepositAccountDto demandDepositAccountDto = demandDepositAccountService.get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toDemandDepositAccountDto();
        long cardAccountNumber = demandDepositAccountDto.getBankCard().getBankCardAccountNumber();
        if (cardAccountNumber == bankCardAccountNumber) {
            int balance = demandDepositAccountDto.getDemandDepositAccountBalance();
            if (balance < withDrawMoney) {
                throw new DemandDepositAccountNotEnoughMoneyException("Not enough money in your account");
            } else {
                demandDepositAccountDto.setDemandDepositAccountBalance(balance - withDrawMoney);
                return demandDepositAccountService.update(demandDepositAccountDto.toDemandDepositAccount()).toDemandDepositAccountDto();
            }
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountNumber.");
        }
    }

    @PutMapping("/{depositAccountIBAN}/transfer/{savingsAccountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getMoneyTransfer(@PathVariable("depositAccountIBAN") String depositAccountIBAN,
                                                    @PathVariable("savingsAccountIBAN") String savingsAccountIBAN,
                                                    @RequestParam("transferMoney") int transferMoney) {
        DemandDepositAccountDto demandDepositAccountDto = demandDepositAccountService.getByAccountIban(depositAccountIBAN).toDemandDepositAccountDto();
        SavingsAccountDto savingsAccountDto = savingsAccountService.getByAccountIban(savingsAccountIBAN).toSavingsAccountDto();
        int demandDepositMoney = demandDepositAccountDto.getDemandDepositAccountBalance();
        int savingsMoney = savingsAccountDto.getSavingsAccountBalance();
        if (demandDepositMoney - transferMoney < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough money in your demandDepositAccount");
        } else {
            if (demandDepositAccountDto.getDemandDepositAccountCurrency().equals(savingsAccountDto.getSavingsAccountCurrency())) {
                demandDepositAccountDto.setDemandDepositAccountBalance(demandDepositMoney - transferMoney);
                savingsAccountDto.setSavingsAccountBalance(savingsMoney + transferMoney);
            } else {
                ExchangeDto exchangeDto = Exchange.getConvert.apply(demandDepositAccountDto.getDemandDepositAccountCurrency());
                demandDepositAccountDto.setDemandDepositAccountBalance(demandDepositMoney - transferMoney);
                savingsAccountDto.setSavingsAccountBalance((int) (savingsMoney + (transferMoney * exchangeDto.getRates().get(savingsAccountDto.getSavingsAccountCurrency()))));
            }
            savingsAccountService.updateBalance(savingsAccountDto.toSavingsAccount()).toSavingsAccountDto();
            return demandDepositAccountService.update(demandDepositAccountDto.toDemandDepositAccount()).toDemandDepositAccountDto();
        }
    }

    @PutMapping("/{fromAccountIBAN}/betweenAccountMoneyTransfer/{toAccountIBAN}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto getBetweenAccountTransferMoney(@PathVariable("fromAccountIBAN") String fromAccountIBAN,
                                                                  @PathVariable("toAccountIBAN") String toAccountIBAN,
                                                                  @RequestParam("transferMoney") int transferMoney) {
        if (fromAccountIBAN != toAccountIBAN) {
            DemandDepositAccountDto fromAccount = demandDepositAccountService.getByAccountIban(fromAccountIBAN).toDemandDepositAccountDto();
            DemandDepositAccountDto toAccount = demandDepositAccountService.getByAccountIban(toAccountIBAN).toDemandDepositAccountDto();
            int fromMoney = fromAccount.getDemandDepositAccountBalance();
            int toMoney = toAccount.getDemandDepositAccountBalance();
            if (fromMoney - transferMoney < 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough money in your demandDepositAccount");
            } else {
                ExchangeDto exchangeDto = Exchange.getConvert.apply(fromAccount.getDemandDepositAccountCurrency());
                fromAccount.setDemandDepositAccountBalance(fromMoney - transferMoney);
                toAccount.setDemandDepositAccountBalance((int) (toMoney + (transferMoney * exchangeDto.getRates().get(toAccount.getDemandDepositAccountCurrency()))));
                demandDepositAccountService.update(toAccount.toDemandDepositAccount()).toDemandDepositAccountDto();
                return demandDepositAccountService.update(fromAccount.toDemandDepositAccount()).toDemandDepositAccountDto();
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Same account.");
        }
    }

    @PutMapping("/{accountNumber}/payDebt/{creditCardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public DemandDepositAccountDto payDebtWithDemandDeposit(@PathVariable("accountNumber") long accountNumber,
                                                            @PathVariable("creditCardNumber") long creditCardNumber,
                                                            @RequestParam("creditCardDebt") int creditCardDebt,
                                                            @RequestParam("minimumPaymentAmount") int minimumPaymentAmount) {
        DemandDepositAccountDto demandDepositAccountDto = demandDepositAccountService.get(accountNumber).
                orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toDemandDepositAccountDto();
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNumber);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        if (demandDepositAccountDto.getDemandDepositAccountCurrency().equals(creditCard.getCurrency())) {
            demandDepositAccountDto.setDemandDepositAccountBalance(demandDepositAccountDto.getDemandDepositAccountBalance() - creditCardDebt - minimumPaymentAmount);
        } else {
            ExchangeDto exchangeDto = Exchange.getConvert.apply(creditCard.getCurrency());
            demandDepositAccountDto.setDemandDepositAccountBalance((int) (
                    demandDepositAccountDto.getDemandDepositAccountBalance() - ((creditCardDebt + minimumPaymentAmount) *
                            exchangeDto.getRates().get(demandDepositAccountDto.getDemandDepositAccountCurrency()))));
        }
        creditCard.setCardDebt(creditCard.getCardDebt() - creditCardDebt);
        extractOfAccount.setTermDebt(extractOfAccount.getTermDebt() - creditCardDebt);
        extractOfAccount.setMinimumPaymentAmount(extractOfAccount.getMinimumPaymentAmount() - minimumPaymentAmount);
        creditCardService.updateCard(creditCard);
        extractOfAccountService.update(extractOfAccount);
        return demandDepositAccountService.update(demandDepositAccountDto.toDemandDepositAccount()).toDemandDepositAccountDto();
    }
}
