package org.kodluyoruz.mybank.account.savingsaccount.concrete;

import org.kodluyoruz.mybank.account.savingsaccount.abtrct.ISavingsAccountService;
import org.kodluyoruz.mybank.account.savingsaccount.abtrct.SavingsAccountRepository;
import org.kodluyoruz.mybank.account.savingsaccount.exception.SavingAccountNotDeletedException;
import org.kodluyoruz.mybank.account.savingsaccount.exception.SavingsAccountNotEnoughMoneyException;
import org.kodluyoruz.mybank.card.bankcard.abstrct.IBankCardService;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCard;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCardDto;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotMatchException;
import org.kodluyoruz.mybank.card.creditcard.abstrct.ICreditCardService;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.customer.abstrct.ICustomerService;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.customer.concrete.CustomerDto;
import org.kodluyoruz.mybank.exchange.Exchange;
import org.kodluyoruz.mybank.extractofaccount.abstrct.IExtractOfAccountService;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.kodluyoruz.mybank.utilities.debtprocess.Debt;
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
    private final ICreditCardService<CreditCard> creditCardService;
    private final IExtractOfAccountService<ExtractOfAccount> extractOfAccountService;
    private final ICustomerService<Customer> customerService;
    private final IBankCardService<BankCard> bankCardService;

    public SavingsAccountService(SavingsAccountRepository savingsAccountRepository, ICreditCardService<CreditCard> creditCardService, IExtractOfAccountService<ExtractOfAccount> extractOfAccountService, ICustomerService<Customer> customerService, IBankCardService<BankCard> bankCardService) {
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
        if ( savingsAccountDto.getBankCard().getBankCardAccountNumber() == bankCardAccountNumber) {
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
        if (savingsAccountDto.getBankCard().getBankCardAccountNumber() == bankCardAccountNumber) {
            if (savingsAccountDto.getSavingsAccountBalance() < withDrawMoney) {
                throw new SavingsAccountNotEnoughMoneyException("Not enough money in your account.");
            } else {
                savingsAccountDto.setSavingsAccountBalance(savingsAccountDto.getSavingsAccountBalance() - withDrawMoney);
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
        double money = String.valueOf(savingsAccountDto.getSavingsAccountCurrency()).equals(String.valueOf(creditCard.getCurrency())) ?
                (creditCardDebt + minimumPaymentAmount) : (creditCardDebt + minimumPaymentAmount) * Exchange.getConvert.apply(String.valueOf(creditCard.getCurrency()))
                .getRates().get(String.valueOf(savingsAccountDto.getSavingsAccountCurrency()));
        savingsAccountDto.setSavingsAccountBalance((int) (savingsAccountDto.getSavingsAccountBalance()-money));
        Debt.debtProcess(creditCardDebt, minimumPaymentAmount, creditCard, extractOfAccount);
        extractOfAccount.setOldDebt(extractOfAccount.getTermDebt());
        extractOfAccount.setMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPaymentAmount));
        creditCardService.updateCard(creditCard);
        extractOfAccountService.update(extractOfAccount);
        return savingsAccountRepository.save(savingsAccountDto.toSavingsAccount());
    }

}
