package org.kodluyoruz.mybank.account.demanddepositaccount.repository;

import org.kodluyoruz.mybank.account.demanddepositaccount.entity.DemandDepositAccount;
import org.springframework.data.repository.CrudRepository;


public interface DemandDepositAccountRepository extends CrudRepository<DemandDepositAccount, Long> {
    DemandDepositAccount findByCustomer_CustomerID(long customerID);
}
