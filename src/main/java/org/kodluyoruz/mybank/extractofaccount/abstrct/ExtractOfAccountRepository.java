package org.kodluyoruz.mybank.extractofaccount.abstrct;

import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;
import org.springframework.data.repository.CrudRepository;

public interface ExtractOfAccountRepository extends CrudRepository<ExtractOfAccount,Integer> {
}
