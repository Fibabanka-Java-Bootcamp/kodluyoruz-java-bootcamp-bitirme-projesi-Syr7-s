package org.kodluyoruz.mybank.account.demanddepositaccount.abstrct;



import org.kodluyoruz.mybank.account.demanddepositaccount.concrete.DemandDepositAccountDto;

import java.util.Optional;

public interface IDemandDepositAccountService<T> {
    T create(T t);
    T create(long customerID, long bankCardAccountNumber, DemandDepositAccountDto demandDepositAccountDto);
    Optional<T> get(long accountNumber);
    T getByAccountIban(String accountIBAN);
    void delete(long accountNumber);
    T depositMoney(long bankCardAccountNumber,long accountNumber,int depositMoney);
    T withDrawMoney(long bankCardAccountNumber,long accountNumber,int withDrawMoney);
    T moneyTransferBetweenDifferentAccounts(String depositAccountIBAN,String savingsAccountIBAN,int transferMoney);
    T moneyTransferBetweenAccounts(String fromAccountIBAN, String toAccountIBAN, int transferMoney);
    T payDebtWithDemandDeposit(long accountNumber,long creditCardNumber,int creditCardDebt,int minimumPaymentAmount);
}
