package org.kodluyoruz.mybank.bankcard.repository;

import org.kodluyoruz.mybank.bankcard.entity.BankCard;
import org.springframework.data.repository.CrudRepository;

public interface BankCardRepository extends CrudRepository<BankCard,Long> {
    BankCard findBankCardByBankCardNO(long bankCardNo);
}
