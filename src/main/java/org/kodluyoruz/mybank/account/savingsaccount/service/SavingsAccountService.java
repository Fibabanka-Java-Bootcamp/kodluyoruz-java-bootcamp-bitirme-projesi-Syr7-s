package org.kodluyoruz.mybank.account.savingsaccount.service;

import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.kodluyoruz.mybank.account.savingsaccount.repository.SavingsAccountRepository;
import org.springframework.stereotype.Service;

@Service
public class SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;

    public SavingsAccountService(SavingsAccountRepository savingsAccountRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
    }
    public SavingsAccount create(SavingsAccount savingsAccount){
        return savingsAccountRepository.save(savingsAccount);
    }
}
