package org.kodluyoruz.mybank.customer.concrete;

import org.apache.log4j.Logger;
import org.kodluyoruz.mybank.customer.abstrct.CustomerService;
import org.kodluyoruz.mybank.customer.exception.CustomerCouldNotDeletedException;
import org.kodluyoruz.mybank.customer.exception.CustomerNotFoundException;
import org.kodluyoruz.mybank.utilities.enums.messages.Messages;
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
    private static final Logger log = Logger.getLogger(CustomerController.class);

    public CustomerController(CustomerService<Customer> customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<URI> create(@RequestBody CustomerDto customerDto) {
        try {
            log.info("Customer will create.");
            CustomerDto editedCustomer = customerService.create(customerDto.toCustomer()).toCustomerDto();
            URI location = ServletUriComponentsBuilder.fromHttpUrl("http://localhost:8080/api/v1/customer")
                    .path("/{customerTC}")
                    .buildAndExpand(editedCustomer.toCustomer().getCustomerTC())
                    .toUri();
            return ResponseEntity.created(location).build();
        } catch (Exception exception) {
            log.error(Messages.Error.AN_ERROR_OCCURRED.message);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Messages.Error.AN_ERROR_OCCURRED.message);
        }
    }

    @GetMapping("/{customerTC}")
    public ResponseEntity<CustomerDto> getCustomerByID(@PathVariable("customerTC") long customerTC) {
        try {
            return ResponseEntity.ok(customerService.getCustomerById(customerTC).toCustomerDto());
        } catch (CustomerNotFoundException exception) {
            log.error(customerTC + " number person could not found.");
            return ResponseEntity.notFound().build();
        } catch (Exception exception) {
            log.error(HttpStatus.INTERNAL_SERVER_ERROR);
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
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (Exception exception) {
            log.error(Messages.Error.SERVER_ERROR.message);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.SERVER_ERROR.message);
        }
    }

    @DeleteMapping("/delete/{customerTC}")
    public String delete(@PathVariable("customerTC") long customerTC) {
        try {
            return customerService.delete(customerTC);
        } catch (CustomerCouldNotDeletedException exception) {
            log.error(exception.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        } catch (Exception exception) {
            log.error(HttpStatus.INTERNAL_SERVER_ERROR);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.Error.SERVER_ERROR.message);
        }
    }

}
