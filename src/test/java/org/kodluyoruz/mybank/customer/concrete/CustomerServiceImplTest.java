package org.kodluyoruz.mybank.customer.concrete;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kodluyoruz.mybank.customer.abstrct.CustomerRepository;
import org.kodluyoruz.mybank.utilities.enums.gender.Gender;
import org.kodluyoruz.mybank.utilities.generate.tcgenerate.TC;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomerServiceImplTest {
    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerServiceImpl customerService;
    static Customer customer = new Customer();

    @BeforeAll
    static void createCustomer() {
        customer.setCustomerTC(Long.parseLong(TC.generateTC.get()));
        customer.setCustomerName("Musa");
        customer.setCustomerLastname("SAYAR");
        customer.setCustomerGender(Gender.ERKEK);
        customer.setCustomerPhone("5399132765");
        customer.setCustomerEmail("musasayar67@gmail.com");
        customer.setCustomerAddress("ISTANBUL");
        customer.setCustomerBirthDate(LocalDate.of(1996, 8, 25));
    }

    @BeforeEach
    void setMockOutput() {
        customerService = new CustomerServiceImpl(customerRepository);
        customerRepository.save(customer);
    }

    @Test
    void getCustomerByCustomerTC() {
        Mockito.when(customerRepository.findCustomerByCustomerTC(customer.getCustomerTC())).thenReturn(customer);
        Assertions.assertNotEquals("Isa", customerService.getCustomerById(customer.getCustomerTC()).getCustomerName());
    }
}