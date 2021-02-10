package org.kodluyoruz.mybank.extractofaccount.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExtractOfAccountNotFound extends RuntimeException {
    public ExtractOfAccountNotFound(String message) {
        super(message);
    }
}
