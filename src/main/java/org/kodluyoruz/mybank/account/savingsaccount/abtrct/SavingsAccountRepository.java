package org.kodluyoruz.mybank.account.savingsaccount.abtrct;

import org.kodluyoruz.mybank.account.savingsaccount.concrete.SavingsAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface SavingsAccountRepository extends CrudRepository<SavingsAccount, Long> {
    Page<SavingsAccount> findAll(Pageable pageable);

    SavingsAccount findSavingsAccountBySavingsAccountIBAN(String accountIBAN);
}
