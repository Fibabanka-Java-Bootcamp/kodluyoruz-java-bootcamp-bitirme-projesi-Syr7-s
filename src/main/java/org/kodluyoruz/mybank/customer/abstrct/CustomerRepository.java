package org.kodluyoruz.mybank.customer.abstrct;

import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer,Long> {
    Page<Customer> findAll(Pageable pageable);
    Customer findCustomerByCustomerTC(long id);
}
