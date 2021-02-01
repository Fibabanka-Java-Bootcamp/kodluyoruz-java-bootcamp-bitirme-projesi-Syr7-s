package org.kodluyoruz.mybank.exchange;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/transaction")
public class ExchangeController {
    @RequestMapping(value = "/convert")
    public ExchangeDto getConvert() {
        RestTemplate restTemplate = new RestTemplate();
        String uri = "https://api.exchangeratesapi.io";
        ExchangeDto exchangeDto = restTemplate.getForObject(uri+"/latest?base=TRY", ExchangeDto.class);
        /*System.out.println(exchangeDto.getRates().get("USD"));
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(exchangeDto));*/
        return exchangeDto;
    }
}
