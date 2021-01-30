package org.kodluyoruz.mybank.bankcard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BankCardNotMatchException extends RuntimeException{
    public BankCardNotMatchException(String message) {
        super(message);
    }
}
