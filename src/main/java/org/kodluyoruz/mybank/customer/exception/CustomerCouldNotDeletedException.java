package org.kodluyoruz.mybank.customer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomerCouldNotDeletedException extends RuntimeException{
    public CustomerCouldNotDeletedException(String message) {
        super(message);
    }
}
