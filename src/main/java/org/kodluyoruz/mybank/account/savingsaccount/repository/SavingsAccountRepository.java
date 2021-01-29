package org.kodluyoruz.mybank.account.savingsaccount.repository;

import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface SavingsAccountRepository extends CrudRepository<SavingsAccount,Integer> {
    Page<SavingsAccount> findAll(Pageable pageable);
}
