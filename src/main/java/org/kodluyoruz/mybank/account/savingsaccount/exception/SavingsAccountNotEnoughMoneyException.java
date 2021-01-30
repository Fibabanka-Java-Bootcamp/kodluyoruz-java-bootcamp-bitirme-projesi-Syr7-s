package org.kodluyoruz.mybank.account.savingsaccount.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SavingsAccountNotEnoughMoneyException extends RuntimeException {
    public SavingsAccountNotEnoughMoneyException(String message) {
        super(message);
    }
}
