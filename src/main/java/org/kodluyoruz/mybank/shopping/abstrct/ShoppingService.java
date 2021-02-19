package org.kodluyoruz.mybank.shopping.abstrct;

import org.kodluyoruz.mybank.shopping.concrete.ShoppingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


public interface ShoppingService<T> {
    T create(T t);

    T doShoppingByCreditCard(long creditCardNo, int password, ShoppingDto shoppingDto);

    T doShoppingByBankCard(long banckCardAccountNumber, long demandDepositAccountNumber, int password, ShoppingDto shoppingDto);

    T getShoppingByProductID(int shoppingID);

    Page<T> getAllShopping(Pageable pageable);
}
