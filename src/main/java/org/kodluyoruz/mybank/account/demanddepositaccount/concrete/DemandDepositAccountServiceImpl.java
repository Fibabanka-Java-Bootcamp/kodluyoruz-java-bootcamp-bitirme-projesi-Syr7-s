package org.kodluyoruz.mybank.account.demanddepositaccount.concrete;


import org.kodluyoruz.mybank.account.demanddepositaccount.abstrct.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.account.demanddepositaccount.abstrct.DemandDepositAccountService;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotDeletedException;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotEnoughMoneyException;
import org.kodluyoruz.mybank.account.savingsaccount.abtrct.SavingsAccountService;
import org.kodluyoruz.mybank.account.savingsaccount.concrete.SavingsAccount;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
public class DemandDepositAccountServiceImpl implements DemandDepositAccountService<DemandDepositAccount> {
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final CustomerService<Customer> customerService;
    private final SavingsAccountService<SavingsAccount> savingsAccountService;
    private final CreditCardService<CreditCard> creditCardService;
    private final ExtractOfAccountService<ExtractOfAccount> extractOfAccountService;
    private final BankCardService<BankCard> bankCardService;

    public DemandDepositAccountServiceImpl(DemandDepositAccountRepository demandDepositAccountRepository, CustomerService<Customer> customerService, SavingsAccountService<SavingsAccount> savingsAccountService, CreditCardService<CreditCard> creditCardService, ExtractOfAccountService<ExtractOfAccount> extractOfAccountService, BankCardService<BankCard> bankCardService) {
        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.customerService = customerService;
        this.savingsAccountService = savingsAccountService;
        this.creditCardService = creditCardService;
        this.extractOfAccountService = extractOfAccountService;
        this.bankCardService = bankCardService;
    }

    @Override
    public DemandDepositAccount create(DemandDepositAccount demandDepositAccount) {
        return demandDepositAccountRepository.save(demandDepositAccount);
    }

    @Override
    public DemandDepositAccount create(long customerTC, long bankCardAccountNumber, DemandDepositAccountDto demandDepositAccountDto) {
        String accountNumber = Account.generateAccount.get();
        demandDepositAccountDto.setDemandDepositAccountNumber(Long.parseLong(accountNumber));
        demandDepositAccountDto.setDemandDepositAccountIBAN(Iban.generateIban.apply(accountNumber));
        CustomerDto customerDto = customerService.getCustomerById(customerTC).toCustomerDto();
        demandDepositAccountDto.setCustomer(customerDto.toCustomer());
        BankCardDto bankCardDto = bankCardService.findBankCard(bankCardAccountNumber).toBankCardDto();
        demandDepositAccountDto.setBankCard(bankCardDto.toBankCard());
        return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
    }

    @Override
    public Optional<DemandDepositAccount> get(long accountNumber) {
        return demandDepositAccountRepository.findById(accountNumber);
    }

    @Override
    public DemandDepositAccount getByAccountIban(String accountIBAN) {
        DemandDepositAccount demandDepositAccount = demandDepositAccountRepository.findDemandDepositAccountByDemandDepositAccountIBAN(accountIBAN);
        if (demandDepositAccount != null) {
            return demandDepositAccount;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found.(AccountIBAN)");
        }
    }

    @Override
    public void delete(long accountNumber) {
        DemandDepositAccount demandDepositAccount = get(accountNumber).
                orElseThrow(() -> (new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")));
        if (demandDepositAccount.getDemandDepositAccountBalance() > 0) {
            throw new DemandDepositAccountNotDeletedException("Demand Deposit Account is not deleted. Because have money in your account");
        } else {
            demandDepositAccountRepository.delete(demandDepositAccount);
        }
    }

    @Override
    public DemandDepositAccount depositMoney(long bankCardAccountNumber, long accountNumber, int depositMoney) {
        DemandDepositAccountDto demandDepositAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toDemandDepositAccountDto();
        if (demandDepositAccountDto.getBankCard().getBankCardAccountNumber() == bankCardAccountNumber) {
            demandDepositAccountDto.setDemandDepositAccountBalance(demandDepositAccountDto.getDemandDepositAccountBalance() + depositMoney);
            return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

    @Override
    public DemandDepositAccount withDrawMoney(long bankCardAccountNumber, long accountNumber, int withDrawMoney) {
        DemandDepositAccountDto demandDepositAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toDemandDepositAccountDto();
        if (demandDepositAccountDto.getBankCard().getBankCardAccountNumber() == bankCardAccountNumber) {
            if (demandDepositAccountDto.getDemandDepositAccountBalance() < withDrawMoney) {
                throw new DemandDepositAccountNotEnoughMoneyException("Not enough money in your account");
            } else {
                demandDepositAccountDto.setDemandDepositAccountBalance(demandDepositAccountDto.getDemandDepositAccountBalance() - withDrawMoney);
                return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
            }
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountNumber.");
        }
    }

    @Override
    public DemandDepositAccount moneyTransferBetweenDifferentAccounts(String depositAccountIBAN, String savingsAccountIBAN, int transferMoney) {
        DemandDepositAccountDto demandDepositAccountDto = getByAccountIban(depositAccountIBAN).toDemandDepositAccountDto();
        SavingsAccount savingAccount = savingsAccountService.getByAccountIban(savingsAccountIBAN);
        if (demandDepositAccountDto.getDemandDepositAccountBalance() - transferMoney < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough money in your demandDepositAccount");
        } else {
            double money = String.valueOf(demandDepositAccountDto.getDemandDepositAccountCurrency()).equals(String.valueOf(savingAccount.getSavingsAccountCurrency())) ?
                    transferMoney : transferMoney * Exchange.getConvert.apply(String.valueOf(demandDepositAccountDto.getDemandDepositAccountCurrency()))
                    .getRates().get(String.valueOf(savingAccount.getSavingsAccountCurrency()));
            demandDepositAccountDto.setDemandDepositAccountBalance(demandDepositAccountDto.getDemandDepositAccountBalance() - transferMoney);
            savingAccount.setSavingsAccountBalance((int) (savingAccount.getSavingsAccountBalance() + money));
            savingsAccountService.update(savingAccount);
            return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
        }
    }

    @Override
    public DemandDepositAccount moneyTransferBetweenAccounts(String fromAccountIBAN, String toAccountIBAN, int transferMoney) {
        if (!fromAccountIBAN.equals(toAccountIBAN)) {
            DemandDepositAccountDto fromAccount = getByAccountIban(fromAccountIBAN).toDemandDepositAccountDto();
            DemandDepositAccountDto toAccount = getByAccountIban(toAccountIBAN).toDemandDepositAccountDto();
            if (fromAccount.getDemandDepositAccountBalance() - transferMoney < 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough money in your demandDepositAccount");
            } else {
                double money = String.valueOf(fromAccount.getDemandDepositAccountCurrency()).equals(String.valueOf(toAccount.getDemandDepositAccountCurrency())) ?
                        transferMoney : transferMoney * Exchange.getConvert.apply(String.valueOf(fromAccount.getDemandDepositAccountCurrency()))
                        .getRates().get(String.valueOf(toAccount.getDemandDepositAccountCurrency()));
                fromAccount.setDemandDepositAccountBalance(fromAccount.getDemandDepositAccountBalance() - transferMoney);
                toAccount.setDemandDepositAccountBalance((int) (toAccount.getDemandDepositAccountBalance() + money));
                demandDepositAccountRepository.save(toAccount.toDemandDepositAccount()).toDemandDepositAccountDto();
                return demandDepositAccountRepository.save(fromAccount.toDemandDepositAccount());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Same account.");
        }
    }

    @Override
    public DemandDepositAccount payDebtWithDemandDeposit(long accountNumber, long creditCardNumber, int creditCardDebt, int minimumPaymentAmount) {
        DemandDepositAccountDto demandDepositAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")).toDemandDepositAccountDto();
        CreditCard creditCard = creditCardService.getCreditCard(creditCardNumber);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        double money = String.valueOf(demandDepositAccountDto.getDemandDepositAccountCurrency()).equals(String.valueOf(creditCard.getCurrency())) ?
                (creditCardDebt + minimumPaymentAmount) : (creditCardDebt + minimumPaymentAmount) *
                Exchange.getConvert.apply(String.valueOf(creditCard.getCurrency()))
                        .getRates().get(String.valueOf(demandDepositAccountDto.getDemandDepositAccountCurrency()));
        demandDepositAccountDto.setDemandDepositAccountBalance((int) (demandDepositAccountDto.getDemandDepositAccountBalance() - money));
        Debt.debtProcess(creditCardDebt, minimumPaymentAmount, creditCard, extractOfAccount);
        extractOfAccount.setOldDebt(extractOfAccount.getTermDebt());
        extractOfAccount.setMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPaymentAmount));
        creditCardService.updateCard(creditCard);
        extractOfAccountService.update(extractOfAccount);
        return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
    }

    @Override
    public Page<DemandDepositAccount> getDemandDepositAccounts(Pageable pageable) {
        return demandDepositAccountRepository.findAll(pageable);
    }


}
