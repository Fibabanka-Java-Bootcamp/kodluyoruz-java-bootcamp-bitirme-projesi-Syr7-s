package org.kodluyoruz.mybank.exchange.concrete;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/transaction")
public class ExchangeController {

    @GetMapping(value = "/convert/{base}")
    public ExchangeDto getConvert(@PathVariable("base") String base) {
        return new RestTemplate()
                .getForObject("https://api.exchangeratesapi.io/latest?base=" + base, ExchangeDto.class);
    }
}
