package org.kodluyoruz.mybank.account.demanddepositaccount.service;

import org.kodluyoruz.mybank.account.demanddepositaccount.dto.DemandDepositAccountDto;
import org.kodluyoruz.mybank.account.demanddepositaccount.entity.DemandDepositAccount;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotDeletedException;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotEnoughMoneyException;
import org.kodluyoruz.mybank.account.demanddepositaccount.repository.DemandDepositAccountRepository;
import org.kodluyoruz.mybank.account.savingsaccount.dto.SavingsAccountDto;
import org.kodluyoruz.mybank.account.savingsaccount.service.SavingsAccountService;
import org.kodluyoruz.mybank.card.bankcard.exception.BankCardNotMatchException;
import org.kodluyoruz.mybank.card.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.card.creditcard.service.CreditCardService;
import org.kodluyoruz.mybank.exchange.Exchange;
import org.kodluyoruz.mybank.exchange.ExchangeDto;
import org.kodluyoruz.mybank.extractofaccount.entity.ExtractOfAccount;
import org.kodluyoruz.mybank.extractofaccount.service.ExtractOfAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
public class DemandDepositAccountService {
    private final DemandDepositAccountRepository demandDepositAccountRepository;
    private final SavingsAccountService savingsAccountService;
    private final CreditCardService creditCardService;
    private final ExtractOfAccountService extractOfAccountService;
    public DemandDepositAccountService(DemandDepositAccountRepository demandDepositAccountRepository, SavingsAccountService savingsAccountService, CreditCardService creditCardService, ExtractOfAccountService extractOfAccountService) {
        this.demandDepositAccountRepository = demandDepositAccountRepository;
        this.savingsAccountService = savingsAccountService;
        this.creditCardService = creditCardService;
        this.extractOfAccountService = extractOfAccountService;
    }

    public DemandDepositAccount create(DemandDepositAccount demandDepositAccount) {
        return demandDepositAccountRepository.save(demandDepositAccount);
    }

    public Optional<DemandDepositAccount> get(long accountIBAN) {
        return demandDepositAccountRepository.findById(accountIBAN);
    }

    public DemandDepositAccount update(DemandDepositAccount demandDepositAccount) {
        return demandDepositAccountRepository.save(demandDepositAccount);
    }

    public DemandDepositAccount getByAccountIban(String accountIBAN) {
        DemandDepositAccount demandDepositAccount = demandDepositAccountRepository.findDemandDepositAccountByDemandDepositAccountIBAN(accountIBAN);
        if (demandDepositAccount != null) {
            return demandDepositAccount;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found.(AccountIBAN)");
        }

    }

    public void delete(long accountNumber) {
        DemandDepositAccount demandDepositAccount = get(accountNumber).
                orElseThrow(() -> (new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found")));
        if (demandDepositAccount.getDemandDepositAccountBalance() > 0) {
            throw new DemandDepositAccountNotDeletedException("Demand Deposit Account is not deleted. Because have money in your account");
        } else {
            demandDepositAccountRepository.delete(demandDepositAccount);
        }
    }

    public DemandDepositAccount depositMoney(long bankCardAccountNumber,long accountNumber,int depositMoney){
        DemandDepositAccountDto demandDepositAccountDto = get(accountNumber).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Account is not found")).toDemandDepositAccountDto();
        long cardAccountNumber = demandDepositAccountDto.getBankCard().getBankCardAccountNumber();
        if (cardAccountNumber == bankCardAccountNumber) {
            int balance = demandDepositAccountDto.getDemandDepositAccountBalance();
            demandDepositAccountDto.setDemandDepositAccountBalance(balance + depositMoney);
            return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
        } else {
            throw new BankCardNotMatchException("BankCard not matched to the accountIBAN.");
        }
    }

    public DemandDepositAccount withDrawMoney(long bankCardAccountNumber,long accountNumber,int withDrawMoney){
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
    public DemandDepositAccount moneyTransferBetweenDifferentAccounts(String depositAccountIBAN,String savingsAccountIBAN,int transferMoney){
        DemandDepositAccountDto demandDepositAccountDto = getByAccountIban(depositAccountIBAN).toDemandDepositAccountDto();
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
            savingsAccountService.update(savingsAccountDto.toSavingsAccount()).toSavingsAccountDto();
            return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
        }
    }
    public DemandDepositAccount moneyTransferBetweenAccounts(String fromAccountIBAN,String toAccountIBAN,int transferMoney){
        if (!fromAccountIBAN.equals(toAccountIBAN)) {
            DemandDepositAccountDto fromAccount = getByAccountIban(fromAccountIBAN).toDemandDepositAccountDto();
            DemandDepositAccountDto toAccount = getByAccountIban(toAccountIBAN).toDemandDepositAccountDto();
            int fromMoney = fromAccount.getDemandDepositAccountBalance();
            int toMoney = toAccount.getDemandDepositAccountBalance();
            if (fromMoney - transferMoney < 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not enough money in your demandDepositAccount");
            } else {
                ExchangeDto exchangeDto = Exchange.getConvert.apply(fromAccount.getDemandDepositAccountCurrency());
                fromAccount.setDemandDepositAccountBalance(fromMoney - transferMoney);
                toAccount.setDemandDepositAccountBalance((int) (toMoney + (transferMoney * exchangeDto.getRates().get(toAccount.getDemandDepositAccountCurrency()))));
                update(toAccount.toDemandDepositAccount()).toDemandDepositAccountDto();
                return update(fromAccount.toDemandDepositAccount());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Same account.");
        }
    }
    public DemandDepositAccount payDebtWithDemandDeposit(long accountNumber,long creditCardNumber,int creditCardDebt,int minimumPaymentAmount){
        DemandDepositAccountDto demandDepositAccountDto = get(accountNumber).orElseThrow(() ->
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
        return demandDepositAccountRepository.save(demandDepositAccountDto.toDemandDepositAccount());
    }
}
