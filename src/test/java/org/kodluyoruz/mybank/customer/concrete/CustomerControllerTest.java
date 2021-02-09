package org.kodluyoruz.mybank.customer.concrete;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kodluyoruz.mybank.customer.abstrct.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.html.HTMLTableCaptionElement;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CustomerControllerTest {
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
        assert location != null;
        Customer editedCustomer = restTemplate.getForObject(location, Customer.class);
        assert editedCustomer != null;
        Assertions.assertEquals("Ugur",editedCustomer.getCustomerName());

    }

    @Test
    void getCustomerById() {
        Customer customer = restTemplate.getForObject("http://localhost:8080/api/customer/22433513943",Customer.class);
        assert customer != null;
        assertEquals("Isa",customer.getCustomerName());
    }
   /*
    @Test
    void customerDelete(){
        String uri = "http://localhost:8080/api/customer/delete/{customerTC}";
        Map<String,Long> params = new HashMap<>();
        params.put("customerTC",81199696053L);
        restTemplate.delete(uri,params);
    }*/


}