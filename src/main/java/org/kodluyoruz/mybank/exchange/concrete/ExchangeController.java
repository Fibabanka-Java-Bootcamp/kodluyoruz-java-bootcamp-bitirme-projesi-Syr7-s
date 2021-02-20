package org.kodluyoruz.mybank.exchange.concrete;

import org.kodluyoruz.mybank.utilities.enums.messages.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/transaction")
public class ExchangeController {

    @GetMapping(value = "/convert/{base}")
    public ExchangeDto getConvert(@PathVariable("base") String base) {
        try {
            return new RestTemplate()
                    .getForObject("https://api.exchangeratesapi.io/latest?base=" + base, ExchangeDto.class);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Messages.Error.CURRENCY_SHOULD_WRITE_UPPER_CASE.message);
        }

    }
}
