package org.kodluyoruz.mybank.account.savingsaccount.concrete;

import org.apache.log4j.Logger;
import org.kodluyoruz.mybank.account.savingsaccount.abtrct.SavingsAccountService;
import org.kodluyoruz.mybank.account.savingsaccount.abtrct.SavingsAccountRepository;
import org.kodluyoruz.mybank.account.savingsaccount.exception.SavingAccountNotDeletedException;
import org.kodluyoruz.mybank.account.savingsaccount.exception.SavingsAccountNotEnoughMoneyException;
import org.kodluyoruz.mybank.card.bankcard.abstrct.BankCardService;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCard;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCardDto;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotMatchException;
import org.kodluyoruz.mybank.card.creditcard.abstrct.CreditCardService;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.customer.abstrct.CustomerService;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.customer.concrete.CustomerDto;
import org.kodluyoruz.mybank.exchange.concrete.Exchange;
import org.kodluyoruz.mybank.extractofaccount.abstrct.ExtractOfAccountService;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.kodluyoruz.mybank.utilities.debtprocess.Debt;
import org.kodluyoruz.mybank.utilities.generate.accountgenerate.Account;
import org.kodluyoruz.mybank.utilities.generate.ibangenerate.Iban;
import org.kodluyoruz.mybank.utilities.messages.ErrorMessages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class SavingsAccountServiceImpl implements SavingsAccountService<SavingsAccount> {
    private final SavingsAccountRepository savingsAccountRepository;
    private final CreditCardService<CreditCard> creditCardService;
    private final ExtractOfAccountService<ExtractOfAccount> extractOfAccountService;
    private final CustomerService<Customer> customerService;
    private final BankCardService<BankCard> bankCardService;
    private static final Logger log = Logger.getLogger(SavingsAccountServiceImpl.class);

    public SavingsAccountServiceImpl(SavingsAccountRepository savingsAccountRepository, CreditCardService<CreditCard> creditCardService, ExtractOfAccountService<ExtractOfAccount> extractOfAccountService, CustomerService<Customer> customerService, BankCardService<BankCard> bankCardService) {
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
    public SavingsAccount create(long customerTC, long bankCardAccountNumber, SavingsAccountDto savingsAccountDto) {
        String accountNumber = Account.generateAccount.get();
        savingsAccountDto.setSavingsAccountNumber(Long.parseLong(accountNumber));
        savingsAccountDto.setSavingsAccountIBAN(Iban.generateIban.apply(accountNumber));
        CustomerDto customerDto = customerService.getCustomerById(customerTC).toCustomerDto();
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
            log.error(ErrorMessages.ACCOUNT_COULD_NOT_FOUND);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.ACCOUNT_COULD_NOT_FOUND);
        }
    }

    @Override
    public void delete(long accountNumber) {
        SavingsAccount savingsAccount = get(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.ACCOUNT_COULD_NOT_FOUND));
        if (savingsAccount.getSavingsAccountBalance() != 0) {
            log.error(ErrorMessages.ACCOUNT_COULD_NOT_DELETED_BECAUSE_HAVE_MONEY_IN_YOUR_ACCOUNT);
            throw new SavingAccountNotDeletedException(ErrorMessages.ACCOUNT_COULD_NOT_DELETED_BECAUSE_HAVE_MONEY_IN_YOUR_ACCOUNT);
        } else {
            savingsAccountRepository.delete(savingsAccount);
        }
    }

    @Override
    public SavingsAccount depositMoney(long bankCardAccountNumber, int password, long accountNumber, int depositMoney) {
        SavingsAccountDto savingsAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.ACCOUNT_COULD_NOT_FOUND)).toSavingsAccountDto();
        if (isMatchBankCardAccountNumberAndPasswordWithSavingsAccount(savingsAccountDto, bankCardAccountNumber, password)) {
            int balance = savingsAccountDto.getSavingsAccountBalance();
            savingsAccountDto.setSavingsAccountBalance(balance + depositMoney);
            return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
        } else {
            log.error(ErrorMessages.CARD_COULD_NOT_MATCHED_TO_YOUR_ACCOUNT);
            throw new BankCardNotMatchException(ErrorMessages.CARD_COULD_NOT_MATCHED_TO_YOUR_ACCOUNT);
        }
    }

    @Override
    public SavingsAccount withDrawMoney(long bankCardAccountNumber, int password, long accountNumber, int withDrawMoney) {
        SavingsAccountDto savingsAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.ACCOUNT_COULD_NOT_FOUND)).toSavingsAccountDto();
        if (isMatchBankCardAccountNumberAndPasswordWithSavingsAccount(savingsAccountDto, bankCardAccountNumber, password)) {
            if (savingsAccountDto.getSavingsAccountBalance() < withDrawMoney) {
                log.error(ErrorMessages.NOT_ENOUGH_MONEY_IN_YOUR_ACCOUNT);
                throw new SavingsAccountNotEnoughMoneyException(ErrorMessages.NOT_ENOUGH_MONEY_IN_YOUR_ACCOUNT);
            } else {
                savingsAccountDto.setSavingsAccountBalance(savingsAccountDto.getSavingsAccountBalance() - withDrawMoney);
                return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
            }
        } else {
            log.error(ErrorMessages.CARD_COULD_NOT_MATCHED_TO_YOUR_ACCOUNT);
            throw new BankCardNotMatchException(ErrorMessages.CARD_COULD_NOT_MATCHED_TO_YOUR_ACCOUNT);
        }
    }

    @Override
    public SavingsAccount payDebtWithAccount(long accountNumber, long creditCardNumber, int creditCardDebt, int minimumPaymentAmount) {
        SavingsAccountDto savingsAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.ACCOUNT_COULD_NOT_FOUND)).toSavingsAccountDto();

        CreditCard creditCard = creditCardService.getCreditCard(creditCardNumber);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        double money = getMoney(creditCardDebt, minimumPaymentAmount, savingsAccountDto, creditCard);
        savingsAccountDto.setSavingsAccountBalance((int) (savingsAccountDto.getSavingsAccountBalance() - money));
        Debt.debtProcess(creditCardDebt, minimumPaymentAmount, creditCard, extractOfAccount);
        extractOfAccount.setOldDebt(extractOfAccount.getTermDebt());
        extractOfAccount.setMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPaymentAmount));
        creditCardService.updateCard(creditCard);
        extractOfAccountService.update(extractOfAccount);
        return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
    }


    @Override
    public SavingsAccount computeSavings(long accountNumber, int termTime, double interestRate, double withHoldingValue) {
        SavingsAccountDto savingsAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.ACCOUNT_COULD_NOT_FOUND)).toSavingsAccountDto();
        double grossInterestReturn = getInterestReturn(termTime, interestRate, savingsAccountDto);
        double netGain = getNetGain(grossInterestReturn, withHoldingValue);
        savingsAccountDto.setTermTime(termTime);
        savingsAccountDto.setGrossInterestReturn(grossInterestReturn);
        savingsAccountDto.setSavingsAccountNetGain(netGain);
        savingsAccountDto.setSavingsAccountInterestRate(interestRate);
        savingsAccountDto.setSavingsAccountBalance((int) (savingsAccountDto.getSavingsAccountBalance() + netGain));
        return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
    }

    private double getNetGain(double grossInterestReturn, double withHoldingValue) {
        return grossInterestReturn - (grossInterestReturn * (withHoldingValue / 100));
    }

    private double getInterestReturn(int termTime, double interestRate, SavingsAccountDto savingsAccountDto) {
        return (savingsAccountDto.getSavingsAccountBalance() * interestRate * termTime) / 36500;
    }

    private double getMoney(int creditCardDebt, int minimumPaymentAmount, SavingsAccountDto savingsAccountDto, CreditCard creditCard) {
        return creditCard.getCardDebt() == creditCardDebt ?
                Exchange.convertProcess(creditCard.getCurrency(), savingsAccountDto.getSavingsAccountCurrency(), creditCardDebt) :
                Exchange.convertProcess(creditCard.getCurrency(), savingsAccountDto.getSavingsAccountCurrency(), (creditCardDebt + minimumPaymentAmount));
    }

    private boolean isMatchBankCardAccountNumberAndPasswordWithSavingsAccount(SavingsAccountDto savingsAccountDto, long bankCardAccountNumber, int password) {
        if (savingsAccountDto.getBankCard().getBankCardAccountNumber() == bankCardAccountNumber) {
            return savingsAccountDto.getBankCard().getBankCardPassword() == password;
        }
        return false;
    }
}
