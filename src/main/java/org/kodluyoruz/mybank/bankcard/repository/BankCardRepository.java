package org.kodluyoruz.mybank.bankcard.repository;

import org.kodluyoruz.mybank.bankcard.entity.BankCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface BankCardRepository extends CrudRepository<BankCard,Long> {
    BankCard findBankCardByBankCardAccountNumber(long bankCardNo);
    Page<BankCard> findAll(Pageable pageable);
}
