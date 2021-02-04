package org.kodluyoruz.mybank.account.demanddepositaccount.service;

import org.kodluyoruz.mybank.account.demanddepositaccount.entity.DemandDepositAccount;
import org.kodluyoruz.mybank.account.demanddepositaccount.exception.DemandDepositAccountNotDeletedException;
import org.kodluyoruz.mybank.account.demanddepositaccount.repository.DemandDepositAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
public class DemandDepositAccountService {
    private final DemandDepositAccountRepository demandDepositAccountRepository;

    public DemandDepositAccountService(DemandDepositAccountRepository demandDepositAccountRepository) {
        this.demandDepositAccountRepository = demandDepositAccountRepository;
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
}
