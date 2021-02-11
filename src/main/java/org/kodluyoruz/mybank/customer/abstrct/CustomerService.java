package org.kodluyoruz.mybank.customer.abstrct;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService<T>{
    T create(T t);
    T getCustomerById(long id);
    T update(T t);
    void delete(long customerId);
    Page<T> customers(Pageable pageable);
}
