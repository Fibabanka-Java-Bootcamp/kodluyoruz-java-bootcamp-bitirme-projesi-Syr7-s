package org.kodluyoruz.mybank.account.demanddepositaccount.service;

import org.kodluyoruz.mybank.account.demanddepositaccount.dto.DemandDepositAccountDto;
import org.kodluyoruz.mybank.account.demanddepositaccount.entity.DemandDepositAccount;
import org.kodluyoruz.mybank.account.demanddepositaccount.repository.DemandDepositAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class DemandDepositAccountService {
    private final DemandDepositAccountRepository demandDepositAccountRepository;

    public DemandDepositAccountService(DemandDepositAccountRepository demandDepositAccountRepository) {
        this.demandDepositAccountRepository = demandDepositAccountRepository;
    }
    public DemandDepositAccount create(DemandDepositAccount demandDepositAccount){
        return demandDepositAccountRepository.save(demandDepositAccount);
    }
    public Optional<DemandDepositAccount> get(int accountIBAN){
        return demandDepositAccountRepository.findById(accountIBAN);
    }
    public DemandDepositAccount update(DemandDepositAccount demandDepositAccount){
       return demandDepositAccountRepository.save(demandDepositAccount);
    }
}
