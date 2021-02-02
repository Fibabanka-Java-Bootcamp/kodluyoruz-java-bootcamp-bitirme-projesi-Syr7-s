package org.kodluyoruz.mybank.exchange;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/transaction")
public class ExchangeController {
    @RequestMapping(value = "/convert/{base}")
    public ExchangeDto getConvert(@PathVariable("base") String base) {
        RestTemplate restTemplate = new RestTemplate();
        String uri = "https://api.exchangeratesapi.io";
        ExchangeDto exchangeDto = restTemplate.getForObject(uri+"/latest?base="+base, ExchangeDto.class);
        /*System.out.println(exchangeDto.getRates().get("USD"));
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(exchangeDto));*/
        return exchangeDto;
    }
}
