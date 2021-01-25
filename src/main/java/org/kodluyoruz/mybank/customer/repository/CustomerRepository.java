package org.kodluyoruz.mybank.customer.repository;

import org.kodluyoruz.mybank.customer.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer,Long> {
    Customer findCustomerByCustomerID(long id);
}
