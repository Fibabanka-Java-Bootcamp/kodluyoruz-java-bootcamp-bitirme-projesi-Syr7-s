package org.kodluyoruz.mybank.account.demanddepositaccount.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DemandDepositAccountNotDeletedException extends RuntimeException {
    public DemandDepositAccountNotDeletedException(String message) {
        super(message);
    }
}
