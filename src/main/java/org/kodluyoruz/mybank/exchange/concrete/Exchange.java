package org.kodluyoruz.mybank.exchange.concrete;


import org.springframework.web.client.RestTemplate;
import org.kodluyoruz.mybank.utilities.enums.currency.Currency;

import java.util.function.Function;

public class Exchange {
    private Exchange() {
    }

    private static final Function<String, ExchangeDto> getConvert = (base -> new RestTemplate()
            .getForObject("https://api.exchangeratesapi.io/latest?base=" + base, ExchangeDto.class)
    );

    public static double convertProcess(Currency currency, Currency convertCurrency, int money) {
        return String.valueOf(currency).equals(String.valueOf(convertCurrency)) ?
                money : money * getConvert.apply(String.valueOf(currency))
                .getRates().get(String.valueOf(convertCurrency));
    }
}
