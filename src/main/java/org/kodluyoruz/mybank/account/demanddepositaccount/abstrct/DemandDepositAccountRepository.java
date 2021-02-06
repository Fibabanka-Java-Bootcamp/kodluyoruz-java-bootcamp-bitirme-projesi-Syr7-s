package org.kodluyoruz.mybank.account.demanddepositaccount.abstrct;

import org.kodluyoruz.mybank.account.demanddepositaccount.concrete.DemandDepositAccount;
import org.springframework.data.repository.CrudRepository;


public interface DemandDepositAccountRepository extends CrudRepository<DemandDepositAccount, Long> {
    DemandDepositAccount findByCustomer_CustomerID(long customerID);
    DemandDepositAccount findDemandDepositAccountByDemandDepositAccountIBAN(String accountIBAN);
}
