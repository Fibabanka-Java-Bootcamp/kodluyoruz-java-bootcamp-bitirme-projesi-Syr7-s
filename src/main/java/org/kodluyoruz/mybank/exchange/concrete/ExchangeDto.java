package org.kodluyoruz.mybank.exchange.concrete;

import lombok.Data;
import java.time.LocalDate;
import java.util.Map;
@Data
public class ExchangeDto {
    private Map<String,Double> rates;
    private String base;
    private LocalDate date;
}
