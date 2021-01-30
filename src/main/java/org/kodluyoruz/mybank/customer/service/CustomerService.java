package org.kodluyoruz.mybank.customer.service;

import org.kodluyoruz.mybank.account.demanddepositaccount.entity.DemandDepositAccount;
import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.kodluyoruz.mybank.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;
import org.kodluyoruz.mybank.customer.entity.Customer;
import org.kodluyoruz.mybank.customer.exception.CustomerCouldNotDeletedException;
import org.kodluyoruz.mybank.customer.exception.CustomerNotFoundException;
import org.kodluyoruz.mybank.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public void deleteCustomer(long id) {
        Customer deletedCustomer = customerRepository.findCustomerByCustomerID(id);
        List<Integer> debts = deletedCustomer.getCreditCards().stream().map(CreditCard::getCardDebt).collect(Collectors.toList());
        List<Integer> demandAccountBalance = deletedCustomer.getDemandDepositAccounts().stream().map(DemandDepositAccount::getDemandDepositAccountBalance).collect(Collectors.toList());
        List<Integer> savingsAccountBalance = deletedCustomer.getSavingsAccounts().stream().map(SavingsAccount::getSavingsAccountBalance).collect(Collectors.toList());
        if (deletedCustomer != null && debts.contains(0) && (demandAccountBalance.contains(0) && savingsAccountBalance.contains(0))) {
            customerRepository.delete(deletedCustomer);
        } else {
            throw new CustomerCouldNotDeletedException("Customer could not deleted .");
        }
    }
}
