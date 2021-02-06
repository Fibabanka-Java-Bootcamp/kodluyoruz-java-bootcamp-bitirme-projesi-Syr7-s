package org.kodluyoruz.mybank.card.bankcard.abstrct;

import org.kodluyoruz.mybank.card.bankcard.concrete.BankCardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBankCardService<T> {
    T create(T t);
    T create(long customerId, BankCardDto bankCardDto);
    T findBankCard(long bankCardNo);
    Page<T> bankCardPage(Pageable pageable);
    void delete(long bankCardNo);
}
