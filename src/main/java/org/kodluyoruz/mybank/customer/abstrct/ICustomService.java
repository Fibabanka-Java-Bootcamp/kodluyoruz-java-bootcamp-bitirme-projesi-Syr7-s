package org.kodluyoruz.mybank.customer.abstrct;

public interface ICustomService<T>{
    T create(T t);
    T getCustomerById(long id);
    T update(T t);
    void delete(long customerId);

}
