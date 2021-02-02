package org.kodluyoruz.mybank.exchange;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.function.Function;

@Component
public class Exchange {

    public static Function<String, ExchangeDto> getConvert = (base -> {
        RestTemplate restTemplate = new RestTemplate();
        String uri = "https://api.exchangeratesapi.io";
        return restTemplate.getForObject(uri + "/latest?base=" + base, ExchangeDto.class);
    });

}
