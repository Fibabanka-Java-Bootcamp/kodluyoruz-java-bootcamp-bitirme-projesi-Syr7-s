package org.kodluyoruz.mybank.shopping.abstrct;

import org.kodluyoruz.mybank.shopping.concrete.ShoppingDto;

public interface IShoppingService<T> {
    T create(T t);
    T doShoppingByCreditCard(long creditCardNo, int password, ShoppingDto shoppingDto);

}
