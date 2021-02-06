package org.kodluyoruz.mybank.customer.abstrct;

import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer,Long> {
    Customer findCustomerByCustomerID(long id);
}
