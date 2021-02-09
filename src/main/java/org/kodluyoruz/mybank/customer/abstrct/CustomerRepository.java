package org.kodluyoruz.mybank.customer.abstrct;

import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer,Long> {
    //Customer findCustomerByCustomerID(long id);
    Customer findCustomerByCustomerTC(long id);
}
