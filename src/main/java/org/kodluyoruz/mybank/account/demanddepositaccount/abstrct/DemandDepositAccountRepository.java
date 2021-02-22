package org.kodluyoruz.mybank.account.demanddepositaccount.abstrct;

import org.kodluyoruz.mybank.account.demanddepositaccount.concrete.DemandDepositAccount;
import org.kodluyoruz.mybank.account.demanddepositaccount.concrete.DemandDepositAccountDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DemandDepositAccountRepository extends CrudRepository<DemandDepositAccount, Long> {
    Page<DemandDepositAccount> findAll(Pageable pageable);

    DemandDepositAccount findDemandDepositAccountByDemandDepositAccountIBAN(String accountIBAN);
}
