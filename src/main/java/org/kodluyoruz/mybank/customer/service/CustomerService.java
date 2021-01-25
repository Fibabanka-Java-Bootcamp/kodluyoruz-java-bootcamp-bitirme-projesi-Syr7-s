package org.kodluyoruz.mybank.customer.service;

import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.entity.Customer;
import org.kodluyoruz.mybank.customer.exception.CustomerNotFoundException;
import org.kodluyoruz.mybank.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer create(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer getCustomerByID(long id) {
        Customer customer = customerRepository.findCustomerByCustomerID(id);
        if (customer != null) {
            return customer;
        } else {
            throw new CustomerNotFoundException("Customer Not Found.");
        }
    }

    public Customer updateCustomer(Customer customer) {
        Customer updatedCustomer = customerRepository.findCustomerByCustomerID(customer.getCustomerID());

        if (updatedCustomer != null) {
            updatedCustomer.setCustomerTC(customer.getCustomerTC());
            updatedCustomer.setCustomerName(customer.getCustomerName());
            updatedCustomer.setCustomerLastname(customer.getCustomerLastname());
            updatedCustomer.setCustomerPhone(customer.getCustomerPhone());
            updatedCustomer.setCustomerEmail(customer.getCustomerEmail());
            updatedCustomer.setCustomerAddress(customer.getCustomerAddress());
            updatedCustomer.setCustomerRemovable(customer.isCustomerRemovable());
            updatedCustomer.setCustomerBirthDate(customer.getCustomerBirthDate());

            return customerRepository.save(updatedCustomer);
        } else {
            throw new CustomerNotFoundException("Customer Not Found.");
        }
    }

    public void deleteCustomer(long id){
        Customer deletedCustomer = customerRepository.findCustomerByCustomerID(id);
        if (deletedCustomer != null){
            customerRepository.delete(deletedCustomer);
        }else{
            throw new CustomerNotFoundException("Customer not found in deleting moment.");
        }
    }
}
