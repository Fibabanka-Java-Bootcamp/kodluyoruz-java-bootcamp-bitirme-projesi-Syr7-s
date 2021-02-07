package org.kodluyoruz.mybank.account.demanddepositaccount.concrete;


import org.kodluyoruz.mybank.account.demanddepositaccount.abstrct.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.account.demanddepositaccount.abstrct.IDemandDepositAccountService;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotDeletedException;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotEnoughMoneyException;
import org.kodluyoruz.mybank.account.savingsaccount.abtrct.ISavingsAccountService;
import org.kodluyoruz.mybank.account.savingsaccount.concrete.SavingsAccount;
import org.kodluyoruz.mybank.card.bankcard.abstrct.IBankCardService;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCard;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCardDto;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotMatchException;
import org.kodluyoruz.mybank.card.creditcard.abstrct.ICreditCardService;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCardService;
import org.kodluyoruz.mybank.customer.abstrct.ICustomerService;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.customer.concrete.CustomerDto;
import org.kodluyoruz.mybank.exchange.Exchange;
import org.kodluyoruz.mybank.exchange.ExchangeDto;
import org.kodluyoruz.mybank.extractofaccount.abstrct.IExtractOfAccountService;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.kodluyoruz.mybank.utilities.debtprocess.Debt;
import org.kodluyoruz.mybank.utilities.generate.accountgenerate.AccountGenerate;
import org.kodluyoruz.mybank.utilities.generate.ibangenerate.IbanGenerate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
public class DemandDepositAccountService implements IDemandDepositAccountService<DemandDepositAccount> {
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final ICustomerService<Customer> customerService;
    private final ISavingsAccountService<SavingsAccount> savingsAccountService;
    private final ICreditCardService<CreditCard> creditCardService;
    private final IExtractOfAccountService<ExtractOfAccount> extractOfAccountService;
    private final IBankCardService<BankCard> bankCardService;

    public DemandDepositAccountService(DemandDepositAccountRepository demandDepositAccountRepository, ICustomerService<Customer> customerService, ISavingsAccountService<SavingsAccount> savingsAccountService, ICreditCardService<CreditCard> creditCardService, IExtractOfAccountService<ExtractOfAccount> extractOfAccountService, IBankCardService<BankCard> bankCardService) {
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
    public DemandDepositAccount create(long customerID, long bankCardAccountNumber, DemandDepositAccountDto demandDepositAccountDto) {
        String accountNumber = AccountGenerate.generateAccount.get();
        demandDepositAccountDto.setDemandDepositAccountNumber(Long.parseLong(accountNumber));
        demandDepositAccountDto.setDemandDepositAccountIBAN(IbanGenerate.generateIban.apply(accountNumber));
        CustomerDto customerDto = customerService.getCustomerById(customerID).toCustomerDto();
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
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account is not found")).toDemandDepositAccountDto();
        long cardAccountNumber = demandDepositAccountDto.getBankCard().getBankCardAccountNumber();
        if (cardAccountNumber == bankCardAccountNumber) {
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
        long cardAccountNumber = demandDepositAccountDto.getBankCard().getBankCardAccountNumber();
        if (cardAccountNumber == bankCardAccountNumber) {
            int balance = demandDepositAccountDto.getDemandDepositAccountBalance();
            if (balance < withDrawMoney) {
                throw new DemandDepositAccountNotEnoughMoneyException("Not enough money in your account");
            } else {
                demandDepositAccountDto.setDemandDepositAccountBalance(balance - withDrawMoney);
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
        int demandDepositMoney = demandDepositAccountDto.getDemandDepositAccountBalance();
        int savingsMoney = savingAccount.getSavingsAccountBalance();
        if (demandDepositMoney - transferMoney < 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough money in your demandDepositAccount");
        } else {
            double money = String.valueOf(demandDepositAccountDto.getDemandDepositAccountCurrency()).equals(String.valueOf(savingAccount.getSavingsAccountCurrency())) ?
                    transferMoney : transferMoney * Exchange.getConvert.apply(String.valueOf(demandDepositAccountDto.getDemandDepositAccountCurrency()))
                    .getRates().get(String.valueOf(savingAccount.getSavingsAccountCurrency()));
            demandDepositAccountDto.setDemandDepositAccountBalance(demandDepositMoney - transferMoney);
            savingAccount.setSavingsAccountBalance((int) (savingsMoney + money));
            savingsAccountService.update(savingAccount);
            return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
        }
    }

    @Override
    public DemandDepositAccount moneyTransferBetweenAccounts(String fromAccountIBAN, String toAccountIBAN, int transferMoney) {
        if (!fromAccountIBAN.equals(toAccountIBAN)) {
            DemandDepositAccountDto fromAccount = getByAccountIban(fromAccountIBAN).toDemandDepositAccountDto();
            DemandDepositAccountDto toAccount = getByAccountIban(toAccountIBAN).toDemandDepositAccountDto();
            int fromMoney = fromAccount.getDemandDepositAccountBalance();
            int toMoney = toAccount.getDemandDepositAccountBalance();
            if (fromMoney - transferMoney < 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough money in your demandDepositAccount");
            } else {
                double money = String.valueOf(fromAccount.getDemandDepositAccountCurrency()).equals(String.valueOf(toAccount.getDemandDepositAccountCurrency())) ?
                        transferMoney : transferMoney * Exchange.getConvert.apply(String.valueOf(fromAccount.getDemandDepositAccountCurrency()))
                        .getRates().get(String.valueOf(toAccount.getDemandDepositAccountCurrency()));
                fromAccount.setDemandDepositAccountBalance(fromMoney - transferMoney);
                toAccount.setDemandDepositAccountBalance((int) (toMoney + money));
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
        demandDepositAccountDto.setDemandDepositAccountBalance((int) (demandDepositAccountDto.getDemandDepositAccountBalance()-money));
        Debt.debtProcess(creditCardDebt, minimumPaymentAmount, creditCard, extractOfAccount);
        extractOfAccount.setOldDebt(extractOfAccount.getTermDebt());
        extractOfAccount.setMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPaymentAmount));
        creditCardService.updateCard(creditCard);
        extractOfAccountService.update(extractOfAccount);
        return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
    }


}
