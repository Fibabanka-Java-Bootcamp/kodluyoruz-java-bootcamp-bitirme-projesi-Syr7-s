package org.kodluyoruz.mybank.account.demanddepositaccount.concrete;


import org.apache.log4j.Logger;
import org.kodluyoruz.mybank.account.demanddepositaccount.abstrct.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.account.demanddepositaccount.abstrct.DemandDepositAccountService;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotDeletedException;
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
import org.kodluyoruz.mybank.utilities.enums.messages.Messages;
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
    private static final Logger log = Logger.getLogger(DemandDepositAccountServiceImpl.class);
    private static final Object lock = new Object();

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
        log.info(customerTC + " customer was created.");
        return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
    }

    @Override
    public DemandDepositAccount update(DemandDepositAccount demandDepositAccount) {
        return demandDepositAccountRepository.save(demandDepositAccount);
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
            log.error(Messages.Error.ACCOUNT_COULD_NOT_FOUND.message);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.ACCOUNT_COULD_NOT_FOUND.message);
        }
    }

    @Override
    public String delete(long accountNumber) {
        DemandDepositAccount demandDepositAccount = get(accountNumber).
                orElseThrow(() -> (new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.ACCOUNT_COULD_NOT_FOUND.message)));
        if (demandDepositAccount.getDemandDepositAccountBalance() != 0) {
            log.info(Messages.Error.ACCOUNT_COULD_NOT_DELETED_BECAUSE_HAVE_MONEY_IN_YOUR_ACCOUNT.message);
            throw new DemandDepositAccountNotDeletedException(Messages.Error.ACCOUNT_COULD_NOT_DELETED_BECAUSE_HAVE_MONEY_IN_YOUR_ACCOUNT.message);
        } else {
            demandDepositAccountRepository.delete(demandDepositAccount);
            return accountNumber + Messages.Info.NUMBERED_ACCOUNT_WAS_SUCCESSFULLY_DELETED.message;
        }
    }

    @Override
    public DemandDepositAccount depositMoney(long bankCardAccountNumber, int password, long accountNumber, int depositMoney) {
        DemandDepositAccountDto demandDepositAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.ACCOUNT_COULD_NOT_FOUND.message)).toDemandDepositAccountDto();
        if (isMatchBankCardNumberAndPasswordWithAccount(demandDepositAccountDto, bankCardAccountNumber, password)) {

                demandDepositAccountDto.setDemandDepositAccountBalance(demandDepositAccountDto.getDemandDepositAccountBalance() + depositMoney);
                return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());

        } else {
            log.error(Messages.Error.CARD_COULD_NOT_MATCHED_TO_YOUR_ACCOUNT.message);
            throw new BankCardNotMatchException(Messages.Error.CARD_COULD_NOT_MATCHED_TO_YOUR_ACCOUNT.message);
        }
    }

    @Override
    public DemandDepositAccount withDrawMoney(long bankCardAccountNumber, int password, long accountNumber, int withDrawMoney) {
        DemandDepositAccountDto demandDepositAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.ACCOUNT_COULD_NOT_FOUND.message)).toDemandDepositAccountDto();
        if (isMatchBankCardNumberAndPasswordWithAccount(demandDepositAccountDto, bankCardAccountNumber, password)) {
            return updateBalanceFromAccount(accountNumber, withDrawMoney);
        } else {
            log.error(Messages.Error.CARD_COULD_NOT_MATCHED_TO_YOUR_ACCOUNT.message);
            throw new BankCardNotMatchException(Messages.Error.CARD_COULD_NOT_MATCHED_TO_YOUR_ACCOUNT.message);
        }
    }

    @Override
    public DemandDepositAccount moneyTransferBetweenDifferentAccounts(String depositAccountIBAN, String savingsAccountIBAN, int transferMoney) {
        DemandDepositAccountDto demandDepositAccountDto = getByAccountIban(depositAccountIBAN).toDemandDepositAccountDto();
        SavingsAccount savingAccount = savingsAccountService.getByAccountIban(savingsAccountIBAN);
        if (demandDepositAccountDto.getDemandDepositAccountBalance() - transferMoney < 0) {
            log.error(Messages.Error.NOT_ENOUGH_MONEY_IN_YOUR_ACCOUNT.message);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.NOT_ENOUGH_MONEY_IN_YOUR_ACCOUNT.message);
        } else {
            double money = Exchange.convertProcess(demandDepositAccountDto.getDemandDepositAccountCurrency(), savingAccount.getSavingsAccountCurrency(), transferMoney);
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
                log.error(Messages.Error.NOT_ENOUGH_MONEY_IN_YOUR_ACCOUNT.message);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.NOT_ENOUGH_MONEY_IN_YOUR_ACCOUNT.message);
            } else {
                double money = Exchange.convertProcess(fromAccount.getDemandDepositAccountCurrency(), toAccount.getDemandDepositAccountCurrency(), transferMoney);
                fromAccount.setDemandDepositAccountBalance(fromAccount.getDemandDepositAccountBalance() - transferMoney);
                toAccount.setDemandDepositAccountBalance((int) (toAccount.getDemandDepositAccountBalance() + money));
                demandDepositAccountRepository.save(toAccount.toDemandDepositAccount()).toDemandDepositAccountDto();
                return demandDepositAccountRepository.save(fromAccount.toDemandDepositAccount());
            }
        } else {
            log.error(Messages.Error.ACCOUNTS_COULD_SAME.message);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.ACCOUNTS_COULD_SAME.message);
        }
    }


    @Override
    public DemandDepositAccount payDebtWithDemandDeposit(long accountNumber, long creditCardNumber, int creditCardDebt, int minimumPaymentAmount) {
        DemandDepositAccountDto demandDepositAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.ACCOUNT_COULD_NOT_FOUND.message)).toDemandDepositAccountDto();

        CreditCard creditCard = creditCardService.getCreditCard(creditCardNumber);
        ExtractOfAccount extractOfAccount = creditCard.getExtractOfAccount();
        double money = getMoney(creditCardDebt, minimumPaymentAmount, demandDepositAccountDto, creditCard);
        demandDepositAccountDto.setDemandDepositAccountBalance((int) (demandDepositAccountDto.getDemandDepositAccountBalance() - money));
        Debt.debtProcess(creditCardDebt, minimumPaymentAmount, creditCard, extractOfAccount);
        extractOfAccount.setOldDebt(extractOfAccount.getTermDebt());
        extractOfAccount.setMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPaymentAmount));
        creditCardService.updateCard(creditCard);
        extractOfAccountService.update(extractOfAccount);
        return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
    }

    @Override
    public DemandDepositAccount updateBalanceFromAccount(long accountNumber, int money) {
        synchronized (lock) {
            DemandDepositAccountDto demandDepositAccountDto = get(accountNumber).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.ACCOUNT_COULD_NOT_FOUND.message)).toDemandDepositAccountDto();
            if (demandDepositAccountDto.getDemandDepositAccountBalance() - money < 0) {
                log.error(Messages.Error.NOT_ENOUGH_MONEY_IN_YOUR_ACCOUNT.message);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.NOT_ENOUGH_MONEY_IN_YOUR_ACCOUNT.message);
            } else {
                demandDepositAccountDto.setDemandDepositAccountBalance(demandDepositAccountDto.getDemandDepositAccountBalance() - money);
                return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
            }
        }
    }

    @Override
    public Page<DemandDepositAccount> getDemandDepositAccounts(Pageable pageable) {
        return demandDepositAccountRepository.findAll(pageable);
    }

    private double getMoney(int creditCardDebt, int minimumPaymentAmount, DemandDepositAccountDto demandDepositAccountDto, CreditCard creditCard) {
        return creditCard.getCardDebt() == creditCardDebt ?
                Exchange.convertProcess(creditCard.getCurrency(), demandDepositAccountDto.getDemandDepositAccountCurrency(), creditCardDebt) :
                Exchange.convertProcess(creditCard.getCurrency(), demandDepositAccountDto.getDemandDepositAccountCurrency(), (creditCardDebt + minimumPaymentAmount));
    }


    private boolean isMatchBankCardNumberAndPasswordWithAccount(DemandDepositAccountDto demandDepositAccountDto, long bankCardAccountNumber, int password) {
        if (demandDepositAccountDto.getBankCard().getBankCardAccountNumber() == bankCardAccountNumber) {
            return demandDepositAccountDto.getBankCard().getBankCardPassword() == password;
        }
        return false;
    }


}
