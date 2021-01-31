package org.kodluyoruz.mybank.creditcard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CreditCardNotCreatedException extends RuntimeException {
    public CreditCardNotCreatedException(String message) {
        super(message);
    }
}
