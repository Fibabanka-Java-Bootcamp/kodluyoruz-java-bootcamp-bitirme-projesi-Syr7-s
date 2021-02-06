package org.kodluyoruz.mybank.account.savingsaccount.concrete;

import org.kodluyoruz.mybank.account.savingsaccount.abtrct.ISavingsAccountService;
import org.kodluyoruz.mybank.account.savingsaccount.abtrct.SavingsAccountRepository;
import org.kodluyoruz.mybank.account.savingsaccount.exception.SavingAccountNotDeletedException;
import org.kodluyoruz.mybank.account.savingsaccount.exception.SavingsAccountNotEnoughMoneyException;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCardDto;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotMatchException;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCardService;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCardService;
import org.kodluyoruz.mybank.customer.concrete.CustomerDto;
import org.kodluyoruz.mybank.customer.concrete.CustomerService;
import org.kodluyoruz.mybank.exchange.Exchange;
import org.kodluyoruz.mybank.exchange.ExchangeDto;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccountService;
import org.kodluyoruz.mybank.utilities.generate.accountgenerate.AccountGenerate;
import org.kodluyoruz.mybank.utilities.generate.ibangenerate.IbanGenerate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class SavingsAccountService implements ISavingsAccountService<SavingsAccount> {
    private final SavingsAccountRepository savingsAccountRepository;
    private final CreditCardService creditCardService;
    private final ExtractOfAccountService extractOfAccountService;
    private final CustomerService customerService;
    private final BankCardService bankCardService;

    public SavingsAccountService(SavingsAccountRepository savingsAccountRepository, CreditCardService creditCardService, ExtractOfAccountService extractOfAccountService, CustomerService customerService, BankCardService bankCardService) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.creditCardService = creditCardService;
        this.extractOfAccountService = extractOfAccountService;
        this.customerService = customerService;
        this.bankCardService = bankCardService;
    }

    @Override
    public SavingsAccount create(SavingsAccount savingsAccount) {
        return savingsAccountRepository.save(savingsAccount);
    }

    @Override
    public SavingsAccount create(long customerID, long bankCardAccountNumber, SavingsAccountDto savingsAccountDto) {
        String accountNumber = AccountGenerate.generateAccount.get();
        savingsAccountDto.setSavingsAccountNumber(Long.parseLong(accountNumber));
        savingsAccountDto.setSavingsAccountIBAN(IbanGenerate.generateIban.apply(accountNumber));
        CustomerDto customerDto = customerService.getCustomerById(customerID).toCustomerDto();
        savingsAccountDto.setCustomer(customerDto.toCustomer());
        BankCardDto bankCardDto = bankCardService.findBankCard(bankCardAccountNumber).toBankCardDto();
        savingsAccountDto.setBankCard(bankCardDto.toBankCard());
        return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
    }

    @Override
    public Optional<SavingsAccount> get(long accountNumber) {
        return savingsAccountRepository.findById(accountNumber);
    }
    @Override
    public SavingsAccount update(SavingsAccount savingsAccount) {
        return savingsAccountRepository.save(savingsAccount);
    }
    @Override
    public Page<SavingsAccount> accounts(Pageable pageable) {
        return savingsAccountRepository.findAll(pageable);
    }

    @Override
    public SavingsAccount getByAccountIban(String accountIban) {
        SavingsAccount savingsAccount = savingsAccountRepository.findSavingsAccountBySavingsAccountIBAN(accountIban);
        if (savingsAccount != null) {
            return savingsAccount;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Savings Account is not found!(AccountIBAN)");
        }
    }

    @Override
    public void delete(long accountNumber) {
        SavingsAccount savingsAccount = get(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found"));
        if (savingsAccount.getSavingsAccountBalance() != 0) {
            throw new SavingAccountNotDeletedException("Savings Account is not deleted.Because you have money in your account.");
        } else {
            savingsAccountRepository.delete(savingsAccount);
        }
    }

    @Override
    public SavingsAccount depositMoney(long bankCardAccountNumber, long accountNumber, int depositMoney) {
        SavingsAccountDto savingsAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toSavingsAccountDto();
        long cardAccountNumber = savingsAccountDto.getBankCard().getBankCardAccountNumber();
        if (cardAccountNumber == bankCardAccountNumber) {
            int balance = savingsAccountDto.getSavingsAccountBalance();
            savingsAccountDto.setSavingsAccountBalance(balance + depositMoney);
            return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

    @Override
    public SavingsAccount withDrawMoney(long bankCardAccountNumber, long accountNumber, int withDrawMoney) {
        SavingsAccountDto savingsAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toSavingsAccountDto();
        long cardAccountNumber = savingsAccountDto.getBankCard().getBankCardAccountNumber();
        if (cardAccountNumber == bankCardAccountNumber) {
            int balance = savingsAccountDto.getSavingsAccountBalance();
            if (balance < withDrawMoney) {
                throw new SavingsAccountNotEnoughMoneyException("Not enough money in your account.");
            } else {
                savingsAccountDto.setSavingsAccountBalance(balance - withDrawMoney);
                return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
            }
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

    @Override
    public SavingsAccount payDebtWithAccount(long accountNumber, long creditCardNumber, int creditCardDebt, int minimumPaymentAmount) {
        SavingsAccountDto savingsAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toSavingsAccountDto();
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNumber);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        if (String.valueOf(savingsAccountDto.getSavingsAccountCurrency()).equals(String.valueOf(creditCard.getCurrency()))) {
            savingsAccountDto.setSavingsAccountBalance(savingsAccountDto.getSavingsAccountBalance() - creditCardDebt - minimumPaymentAmount);
        } else {
            ExchangeDto exchangeDto = Exchange.getConvert.apply(String.valueOf(creditCard.getCurrency()));
            savingsAccountDto.setSavingsAccountBalance((int) (savingsAccountDto.getSavingsAccountBalance() - ((creditCardDebt + minimumPaymentAmount) *
                    exchangeDto.getRates().get(String.valueOf(savingsAccountDto.getSavingsAccountCurrency())))));
        }
        creditCard.setCardDebt(creditCard.getCardDebt() - creditCardDebt);
        extractOfAccount.setTermDebt(extractOfAccount.getTermDebt() - creditCardDebt);
        extractOfAccount.setMinimumPaymentAmount(extractOfAccount.getMinimumPaymentAmount() - minimumPaymentAmount);
        creditCardService.updateCard(creditCard);
        extractOfAccountService.update(extractOfAccount);
        return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
    }

/*
    public SavingsAccount create(SavingsAccount savingsAccount) {
        return savingsAccountRepository.save(savingsAccount);
    }

    public SavingsAccount create(long customerID,long bankCardAccountNumber,SavingsAccountDto savingsAccountDto){
        String accountNumber = AccountGenerate.generateAccount.get();
        savingsAccountDto.setSavingsAccountNumber(Long.parseLong(accountNumber));
        savingsAccountDto.setSavingsAccountIBAN(IbanGenerate.generateIban.apply(accountNumber));
        CustomerDto customerDto = customerService.getCustomerByID(customerID).toCustomerDto();
        savingsAccountDto.setCustomer(customerDto.toCustomer());
        BankCardDto bankCardDto = bankCardService.findBankCard(bankCardAccountNumber).toBankCardDto();
        savingsAccountDto.setBankCard(bankCardDto.toBankCard());
        return  savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
    }

    public Optional<SavingsAccount> get(long accountIBAN) {
        return savingsAccountRepository.findById(accountIBAN);
    }

    public Page<SavingsAccount> savingsAccounts(Pageable pageable) {
        return savingsAccountRepository.findAll(pageable);
    }

    public SavingsAccount update(SavingsAccount savingsAccount) {
        return savingsAccountRepository.save(savingsAccount);
    }

    public SavingsAccount getByAccountIban(String accountIBAN) {
        SavingsAccount savingsAccount = savingsAccountRepository.findSavingsAccountBySavingsAccountIBAN(accountIBAN);
        if (savingsAccount != null) {
            return savingsAccount;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Savings Account is not found!(AccountIBAN)");
        }
    }

    public void delete(long accountNumber) {
        SavingsAccount savingsAccount = get(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found"));
        if (savingsAccount.getSavingsAccountBalance() != 0) {
            throw new SavingAccountNotDeletedException("Savings Account is not deleted.Because you have money in your account.");
        } else {
            savingsAccountRepository.delete(savingsAccount);
        }
    }

    public SavingsAccount depositMoney(long bankCardAccountNumber, long accountNumber, int depositMoney) {
        SavingsAccountDto savingsAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toSavingsAccountDto();
        long cardAccountNumber = savingsAccountDto.getBankCard().getBankCardAccountNumber();
        if (cardAccountNumber == bankCardAccountNumber) {
            int balance = savingsAccountDto.getSavingsAccountBalance();
            savingsAccountDto.setSavingsAccountBalance(balance + depositMoney);
            return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

    public SavingsAccount withDrawMoney(long bankCardAccountNumber, long accountNumber, int  withDrawMoney){
        SavingsAccountDto savingsAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toSavingsAccountDto();
        long cardAccountNumber = savingsAccountDto.getBankCard().getBankCardAccountNumber();
        if (cardAccountNumber == bankCardAccountNumber) {
            int balance = savingsAccountDto.getSavingsAccountBalance();
            if (balance < withDrawMoney) {
                throw new SavingsAccountNotEnoughMoneyException("Not enough money in your account.");
            } else {
                savingsAccountDto.setSavingsAccountBalance(balance - withDrawMoney);
                return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
            }
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }
    public SavingsAccount payDebtWithSavingAccount(long accountNumber,long creditCardNumber,int creditCardDebt,int minimumPaymentAmount){
        SavingsAccountDto savingsAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toSavingsAccountDto();
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNumber);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        if (String.valueOf(savingsAccountDto.getSavingsAccountCurrency()).equals(String.valueOf(creditCard.getCurrency()))) {
            savingsAccountDto.setSavingsAccountBalance(savingsAccountDto.getSavingsAccountBalance() - creditCardDebt - minimumPaymentAmount);
        } else {
            ExchangeDto exchangeDto = Exchange.getConvert.apply(String.valueOf(creditCard.getCurrency()));
            savingsAccountDto.setSavingsAccountBalance((int) (savingsAccountDto.getSavingsAccountBalance() - ((creditCardDebt + minimumPaymentAmount) *
                    exchangeDto.getRates().get(String.valueOf(savingsAccountDto.getSavingsAccountCurrency())))));
        }
        creditCard.setCardDebt(creditCard.getCardDebt() - creditCardDebt);
        extractOfAccount.setTermDebt(extractOfAccount.getTermDebt() - creditCardDebt);
        extractOfAccount.setMinimumPaymentAmount(extractOfAccount.getMinimumPaymentAmount() - minimumPaymentAmount);
        creditCardService.updateCard(creditCard);
        extractOfAccountService.update(extractOfAccount);
        return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
    }

    */

}
