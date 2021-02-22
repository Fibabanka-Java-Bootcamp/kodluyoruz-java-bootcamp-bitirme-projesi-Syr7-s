package org.kodluyoruz.mybank.card.bankcard.abstrct;

import org.kodluyoruz.mybank.card.bankcard.concrete.BankCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankCardRepository extends CrudRepository<BankCard, Long> {
    BankCard findBankCardByBankCardAccountNumber(long bankCardNo);

    Page<BankCard> findAll(Pageable pageable);
}
