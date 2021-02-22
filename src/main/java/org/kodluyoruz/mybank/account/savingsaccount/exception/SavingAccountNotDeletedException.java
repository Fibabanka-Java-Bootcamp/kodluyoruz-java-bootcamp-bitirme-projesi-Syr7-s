package org.kodluyoruz.mybank.account.savingsaccount.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SavingAccountNotDeletedException extends RuntimeException {
    public SavingAccountNotDeletedException(String message) {
        super(message);
    }
}
