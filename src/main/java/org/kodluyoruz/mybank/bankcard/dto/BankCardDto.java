package org.kodluyoruz.mybank.bankcard.dto;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.bankcard.entity.BankCard;

import java.time.LocalDate;

@Data
@Builder
public class BankCardDto {
    private long bankCardNo;
    private String bankCardNameSurname;
    private LocalDate bankCardExpirationDate;
    private String securityCode;

    public BankCard toBankCard(){
        return BankCard.builder()
                .bankCardNO(this.bankCardNo)
                .bankCardNameSurname(this.bankCardNameSurname)
                .bankCardExpirationDate(this.bankCardExpirationDate)
                .securityCode(this.securityCode)
                .build();
    }
}
