package org.kodluyoruz.mybank.customer.concrete;

import org.kodluyoruz.mybank.customer.abstrct.CustomerService;
import org.kodluyoruz.mybank.customer.exception.CustomerCouldNotDeletedException;
import org.kodluyoruz.mybank.customer.exception.CustomerNotFoundException;
import org.kodluyoruz.mybank.utilities.messages.ErrorMessages;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {
    private final CustomerService<Customer> customerService;

    public CustomerController(CustomerService<Customer> customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<URI> create(@RequestBody CustomerDto customerDto) {
        try {
            CustomerDto editedCustomer = customerService.create(customerDto.toCustomer()).toCustomerDto();
            URI location = ServletUriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/customer")
                    .path("/{customerTC}")
                    .buildAndExpand(editedCustomer.toCustomer().getCustomerTC())
                    .toUri();
            return ResponseEntity.created(location).build();
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.AN_ERROR_OCCURRED);
        }
    }

    @GetMapping("/{customerTC}")
    public ResponseEntity<CustomerDto> getCustomerByID(@PathVariable("customerTC") long customerTC) {
        try {
            return ResponseEntity.ok(customerService.getCustomerById(customerTC).toCustomerDto());
        } catch (CustomerNotFoundException exception) {
            return ResponseEntity.notFound().build();
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/customers", params = {"page", "size"})
    public List<CustomerDto> getCustomers(@Min(value = 0) @RequestParam("page") int page, @Min(value = 1) @RequestParam("size") int size) {
        return customerService.getCustomers(PageRequest.of(page, size)).stream()
                .map(Customer::toCustomerDto)
                .collect(Collectors.toList());
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto update(@RequestBody CustomerDto customerDto) {
        try {
            return customerService.update(customerDto.toCustomer()).toCustomerDto();
        } catch (CustomerNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.AN_ERROR_OCCURRED);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{customerTC}")
    public void delete(@PathVariable("customerTC") long customerTC) {
        try {
            customerService.delete(customerTC);
        } catch (CustomerCouldNotDeletedException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.AN_ERROR_OCCURRED);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.SERVER_ERROR);
        }
    }

}
