package org.kodluyoruz.mybank.account.savingsaccount.abtrct;

import org.kodluyoruz.mybank.account.savingsaccount.concrete.SavingsAccountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SavingsAccountService<T> {
    T create(T t);

    T create(long customerID, long bankCardAccountNumber, SavingsAccountDto savingsAccountDto);

    Optional<T> get(long accountNumber);

    T update(T t);

    Page<T> accounts(Pageable pageable);

    T getByAccountIban(String accountIban);

    String delete(long accountNumber);

    T depositMoney(long bankCardAccountNumber, int password, long accountNumber, int depositMoney);

    T withDrawMoney(long bankCardAccountNumber, int password, long accountNumber, int withDrawMoney);

    T payDebtWithAccount(long accountNumber, long creditCardNumber, int creditCardDebt, int minimumPaymentAmount);

    T computeSavings(long accountNumber, int termTime, double interestRate, double withHoldingValue);
}
