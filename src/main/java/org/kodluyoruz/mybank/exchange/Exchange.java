package org.kodluyoruz.mybank.exchange;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.function.Function;

@Component
public class Exchange {
    public static Function<String, ExchangeDto> getConvert = (base ->  new RestTemplate()
               .getForObject("https://api.exchangeratesapi.io/latest?base="+base,ExchangeDto.class)
    );
}
