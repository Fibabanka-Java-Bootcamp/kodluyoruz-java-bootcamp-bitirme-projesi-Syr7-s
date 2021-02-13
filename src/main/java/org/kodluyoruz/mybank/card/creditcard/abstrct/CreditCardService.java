package org.kodluyoruz.mybank.card.creditcard.abstrct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CreditCardService<T>{
    T create(T t);
    Page<T> cards(Pageable pageable);
    T getCreditCard(long creditCardNo);
    T updateCard(T t);
    T payCreditCardDebt(long creditCardNO,int password,int payMoney, double minimumPayment);
    T debtPaymentWithoutCreditCard(long creditCardAccountNO, int payMoney, double minimumPayment);
}
