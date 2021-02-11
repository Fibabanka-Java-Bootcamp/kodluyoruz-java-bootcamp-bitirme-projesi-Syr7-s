package org.kodluyoruz.mybank.account.demanddepositaccount.abstrct;

import javafx.scene.control.Pagination;
import org.kodluyoruz.mybank.account.demanddepositaccount.concrete.DemandDepositAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandDepositAccountRepository extends CrudRepository<DemandDepositAccount, Long> {
    Page<DemandDepositAccount> findAll(Pagination pagination);
    DemandDepositAccount findDemandDepositAccountByDemandDepositAccountIBAN(String accountIBAN);
}
