package org.kodluyoruz.mybank.account.savingsaccount.service;

import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.kodluyoruz.mybank.account.savingsaccount.repository.SavingsAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;

    public SavingsAccountService(SavingsAccountRepository savingsAccountRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
    }
    public SavingsAccount create(SavingsAccount savingsAccount){
        return savingsAccountRepository.save(savingsAccount);
    }
    public Optional<SavingsAccount> get(int accountIBAN){
        return savingsAccountRepository.findById(accountIBAN);
    }
}
