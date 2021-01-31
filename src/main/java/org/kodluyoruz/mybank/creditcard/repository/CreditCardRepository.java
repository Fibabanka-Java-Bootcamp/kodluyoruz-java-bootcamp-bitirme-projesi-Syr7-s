package org.kodluyoruz.mybank.creditcard.repository;

import org.kodluyoruz.mybank.creditcard.entity.CreditCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface CreditCardRepository extends CrudRepository<CreditCard,Long> {
    Page<CreditCard> findAll(Pageable pageable);
    CreditCard findCreditCardByCardNO(long creditCardNo);
}
