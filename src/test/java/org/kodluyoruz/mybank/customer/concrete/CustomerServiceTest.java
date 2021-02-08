package org.kodluyoruz.mybank.customer.concrete;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kodluyoruz.mybank.customer.abstrct.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {
    RestTemplate restTemplate;
    Customer customer = new Customer();

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
    }

    @Test
    void create() {
        customer.setCustomerName("Ugur");
        URI location = restTemplate.postForLocation("http://localhost:8080/api/customer", customer);
        Customer editedCustomer = restTemplate.getForObject(location, Customer.class);
        Assert.assertEquals("Ugur",editedCustomer.getCustomerName());

    }

    @Test
    void getCustomerById() {
        Customer customer = restTemplate.getForObject("http://localhost:8080/api/customer/22433513943",Customer.class);
        Assert.assertEquals("Isa",customer.getCustomerName());
    }
}