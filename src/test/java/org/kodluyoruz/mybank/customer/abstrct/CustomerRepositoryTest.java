package org.kodluyoruz.mybank.customer.abstrct;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;
    @Test
    void findCustomerByCustomerTC() {
        Customer customer = customerRepository.findCustomerByCustomerTC(22433513943L);
        Assertions.assertEquals("Isa",customer.getCustomerName());
    }
    @Test
    void deleteCustomer(){
        Customer customer = customerRepository.findCustomerByCustomerTC(42311352391L);
        customerRepository.delete(customer);
    }
}