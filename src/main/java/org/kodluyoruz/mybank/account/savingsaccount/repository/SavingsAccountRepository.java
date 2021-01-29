package org.kodluyoruz.mybank.account.savingsaccount.repository;

import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.springframework.data.repository.CrudRepository;

public interface SavingsAccountRepository extends CrudRepository<SavingsAccount,Integer> {
}
