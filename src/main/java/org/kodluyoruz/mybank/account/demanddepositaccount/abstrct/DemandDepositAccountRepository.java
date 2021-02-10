package org.kodluyoruz.mybank.account.demanddepositaccount.abstrct;

import org.kodluyoruz.mybank.account.demanddepositaccount.concrete.DemandDepositAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandDepositAccountRepository extends CrudRepository<DemandDepositAccount, Long> {
    DemandDepositAccount findDemandDepositAccountByDemandDepositAccountIBAN(String accountIBAN);
}
