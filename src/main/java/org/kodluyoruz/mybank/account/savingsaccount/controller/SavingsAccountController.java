package org.kodluyoruz.mybank.account.savingsaccount.controller;

import org.kodluyoruz.mybank.account.savingsaccount.dto.SavingsAccountDto;
import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.kodluyoruz.mybank.account.savingsaccount.exception.SavingsAccountNotEnoughMoneyException;
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
    private final CreditCardService creditCardService;
    private final ExtractOfAccountService extractOfAccountService;

    public SavingsAccountController(SavingsAccountService savingsAccountService, CustomerService customerService, BankCardService bankCardService, CreditCardService creditCardService, ExtractOfAccountService extractOfAccountService) {
        this.savingsAccountService = savingsAccountService;
        this.customerService = customerService;
        this.bankCardService = bankCardService;
        this.creditCardService = creditCardService;
        this.extractOfAccountService = extractOfAccountService;
    }

    @PostMapping("/{customerID}/account/{bankCardAccountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto create(@PathVariable("customerID") long customerID, @PathVariable("bankCardAccountNumber") long bankCardAccountNumber, @RequestBody SavingsAccountDto savingsAccountDto) {
        String accountNumber = AccountGenerate.accountGenerate.get();
        savingsAccountDto.setSavingsAccountNumber(Long.parseLong(accountNumber));
        savingsAccountDto.setSavingsAccountIBAN(IbanGenerate.ibanGenerate.apply(accountNumber));
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        savingsAccountDto.setCustomer(customerDto.toCustomer());
        BankCardDto bankCardDto = bankCardService.findBankCard(bankCardAccountNumber).toBankCardDto();
        savingsAccountDto.setBankCard(bankCardDto.toBankCard());
        return savingsAccountService.create(savingsAccountDto.toSavingsAccount()).toSavingsAccountDto();
    }

    @GetMapping("/{accountIBAN}")
    public SavingsAccountDto get(@PathVariable("accountIBAN") long accountIBAN) {
        return savingsAccountService.get(accountIBAN).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Savings Account is not found")).toSavingsAccountDto();
    }

    @GetMapping(value = "/accounts", params = {"page", "size"})
    public List<SavingsAccountDto> getAllSavingsAccount(@Min(value = 0) @RequestParam("page") int page, @Min(value = 1) @RequestParam("size") int size) {
        return savingsAccountService.savingsAccounts(PageRequest.of(page, size)).stream()
                .map(SavingsAccount::toSavingsAccountDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/{bankCardAccountNumber}/deposit/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto getUpdateSavings(@PathVariable("bankCardAccountNumber") long bankCardAccountNumber,
                                              @PathVariable("accountNumber") long accountNumber, @RequestParam("depositMoney") int depositMoney) {
        SavingsAccountDto savingsAccountDto = savingsAccountService.get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toSavingsAccountDto();
        long cardAccountNumber = savingsAccountDto.getBankCard().getBankCardAccountNumber();
        if (cardAccountNumber == bankCardAccountNumber) {
            int balance = savingsAccountDto.getSavingsAccountBalance();
            savingsAccountDto.setSavingsAccountBalance(balance + depositMoney);
            return savingsAccountService.updateBalance(savingsAccountDto.toSavingsAccount()).toSavingsAccountDto();
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

    @PutMapping("/{bankCardAccountNumber}/withDrawMoney/{accountNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto getUpdateSavingsWithDrawMoney(@PathVariable("bankCardAccountNumber") long bankCardAccountNumber,
                                                           @PathVariable("accountNumber") long accountNumber, @RequestParam("withDrawMoney") int withDrawMoney) {
        SavingsAccountDto savingsAccountDto = savingsAccountService.get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toSavingsAccountDto();
        long cardAccountNumber = savingsAccountDto.getBankCard().getBankCardAccountNumber();
        if (cardAccountNumber == bankCardAccountNumber) {
            int balance = savingsAccountDto.getSavingsAccountBalance();
            if (balance < withDrawMoney) {
                throw new SavingsAccountNotEnoughMoneyException("Not enough money in your account.");
            } else {
                savingsAccountDto.setSavingsAccountBalance(balance - withDrawMoney);
                return savingsAccountService.updateBalance(savingsAccountDto.toSavingsAccount()).toSavingsAccountDto();
            }
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

    @PutMapping("/{accountNumber}/payDebt/{creditCardNumber}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto payDebtWithSaving(@PathVariable("accountNumber") long accountNumber,
                                               @PathVariable("creditCardNumber") long creditCardNumber,
                                               @RequestParam("creditCardDebt") int creditCardDebt,
                                               @RequestParam("minimumPaymentAmount") int minimumPaymentAmount) {
        SavingsAccountDto savingsAccountDto = savingsAccountService.get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toSavingsAccountDto();
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNumber);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();

        if (savingsAccountDto.getSavingsAccountCurrency().equals(creditCard.getCurrency())) {
            savingsAccountDto.setSavingsAccountBalance(savingsAccountDto.getSavingsAccountBalance() - creditCardDebt - minimumPaymentAmount);
        } else {
            ExchangeDto exchangeDto = Exchange.getConvert.apply(creditCard.getCurrency());
            savingsAccountDto.setSavingsAccountBalance((int) (savingsAccountDto.getSavingsAccountBalance() - ((creditCardDebt + minimumPaymentAmount) *
                    exchangeDto.getRates().get(savingsAccountDto.getSavingsAccountCurrency()))));
        }
        creditCard.setCardDebt(creditCard.getCardDebt() - creditCardDebt);
        extractOfAccount.setTermDebt(extractOfAccount.getTermDebt() - creditCardDebt);
        extractOfAccount.setMinimumPaymentAmount(extractOfAccount.getMinimumPaymentAmount() - minimumPaymentAmount);
        creditCardService.updateCard(creditCard);
        extractOfAccountService.update(extractOfAccount);
        return savingsAccountService.updateBalance(savingsAccountDto.toSavingsAccount()).toSavingsAccountDto();
    }
}
