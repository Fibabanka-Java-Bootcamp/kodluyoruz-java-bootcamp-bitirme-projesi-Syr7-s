package org.kodluyoruz.mybank.card.creditcard.repository;

import org.kodluyoruz.mybank.card.creditcard.entity.CreditCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface CreditCardRepository extends CrudRepository<CreditCard,Long> {
    Page<CreditCard> findAll(Pageable pageable);
    CreditCard findCreditCardByCardAccountNumber(long creditCardNo);
}
