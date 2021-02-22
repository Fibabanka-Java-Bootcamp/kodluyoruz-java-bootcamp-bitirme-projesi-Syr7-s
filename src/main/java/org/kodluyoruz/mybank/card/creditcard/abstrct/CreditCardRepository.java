package org.kodluyoruz.mybank.card.creditcard.abstrct;

import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditCardRepository extends CrudRepository<CreditCard, Long> {
    Page<CreditCard> findAll(Pageable pageable);

    CreditCard findCreditCardByCardAccountNumber(long creditCardNo);
}
