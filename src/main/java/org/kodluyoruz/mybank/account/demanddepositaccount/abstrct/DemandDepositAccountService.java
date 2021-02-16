package org.kodluyoruz.mybank.account.demanddepositaccount.abstrct;


import org.kodluyoruz.mybank.account.demanddepositaccount.concrete.DemandDepositAccountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DemandDepositAccountService<T> {
    T create(T t);

    T create(long customerID, long bankCardAccountNumber, DemandDepositAccountDto demandDepositAccountDto);

    T update(T t);

    Optional<T> get(long accountNumber);

    T getByAccountIban(String accountIBAN);

    void delete(long accountNumber);

    T depositMoney(long bankCardAccountNumber, int password, long accountNumber, int depositMoney);

    T withDrawMoney(long bankCardAccountNumber, int password, long accountNumber, int withDrawMoney);

    T moneyTransferBetweenDifferentAccounts(String depositAccountIBAN, String savingsAccountIBAN, int transferMoney);

    T moneyTransferBetweenAccounts(String fromAccountIBAN, String toAccountIBAN, int transferMoney);

    T payDebtWithDemandDeposit(long accountNumber, long creditCardNumber, int creditCardDebt, int minimumPaymentAmount);

    T updateBalanceFromAccount(long accountNumber, int money);

    Page<T> getDemandDepositAccounts(Pageable pageable);
}
