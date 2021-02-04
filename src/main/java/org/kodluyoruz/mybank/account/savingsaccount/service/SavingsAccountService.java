package org.kodluyoruz.mybank.account.savingsaccount.service;

import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.kodluyoruz.mybank.account.savingsaccount.exception.SavingAccountNotDeletedException;
import org.kodluyoruz.mybank.account.savingsaccount.repository.SavingsAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;

    public SavingsAccountService(SavingsAccountRepository savingsAccountRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
    }

    public SavingsAccount create(SavingsAccount savingsAccount) {
        return savingsAccountRepository.save(savingsAccount);
    }

    public Optional<SavingsAccount> get(long accountIBAN) {
        return savingsAccountRepository.findById(accountIBAN);
    }

    public Page<SavingsAccount> savingsAccounts(Pageable pageable) {
        return savingsAccountRepository.findAll(pageable);
    }

    public SavingsAccount updateBalance(SavingsAccount savingsAccount) {
        return savingsAccountRepository.save(savingsAccount);
    }

    public SavingsAccount getByAccountIban(String accountIBAN) {
        SavingsAccount savingsAccount = savingsAccountRepository.findSavingsAccountBySavingsAccountIBAN(accountIBAN);
        if (savingsAccount != null) {
            return savingsAccount;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Savings Account is not found!(AccountIBAN)");
        }
    }

    public void delete(long accountNumber) {
        SavingsAccount savingsAccount = get(accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account is not found"));
        if (savingsAccount.getSavingsAccountBalance() != 0) {
            throw new SavingAccountNotDeletedException("Savings Account is not deleted.Because you have money in your account.");
        } else {
            savingsAccountRepository.delete(savingsAccount);
        }

    }
}
