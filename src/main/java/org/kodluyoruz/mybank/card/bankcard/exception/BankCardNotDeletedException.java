package org.kodluyoruz.mybank.card.bankcard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BankCardNotDeletedException extends RuntimeException {
    public BankCardNotDeletedException(String message) {
        super(message);
    }
}
