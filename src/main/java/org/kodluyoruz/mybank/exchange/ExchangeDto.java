package org.kodluyoruz.mybank.exchange;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

import java.util.Map;
@Data
public class ExchangeDto {
    private Map<String,Double> rates;
    private String base;
    private LocalDate date;
}
