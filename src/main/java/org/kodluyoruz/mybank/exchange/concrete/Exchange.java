package org.kodluyoruz.mybank.exchange.concrete;


import org.springframework.web.client.RestTemplate;
import java.util.function.Function;

public class Exchange {
    private Exchange() {
    }

    public static final Function<String, ExchangeDto> getConvert = (base -> new RestTemplate()
            .getForObject("https://api.exchangeratesapi.io/latest?base=" + base, ExchangeDto.class)
    );
}
