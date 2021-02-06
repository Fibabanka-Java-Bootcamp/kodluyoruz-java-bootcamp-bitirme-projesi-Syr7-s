package org.kodluyoruz.mybank.customer.concrete;

import org.kodluyoruz.mybank.customer.exception.CustomerCouldNotDeletedException;
import org.kodluyoruz.mybank.customer.exception.CustomerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto create(@RequestBody CustomerDto customerDto) {
        try {
            return customerService.create(customerDto.toCustomer()).toCustomerDto();
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "An error occurred");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerByID(@PathVariable("id") long id) {
        try{
            return ResponseEntity.ok(customerService.getCustomerById(id).toCustomerDto());
        }catch (CustomerNotFoundException exception){
            return ResponseEntity.notFound().build();
        }catch (Exception exception){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto update(@RequestBody CustomerDto customerDto){
        try{
            return customerService.update(customerDto.toCustomer()).toCustomerDto();
        }catch (CustomerNotFoundException exception){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"An error occurred");
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Server Error");
        }
    }
    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable("id") long id){
        try{
            customerService.delete(id);
        }catch (CustomerCouldNotDeletedException exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"An error occurred");
        }catch (Exception exception){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Server Error");
        }
    }

}
