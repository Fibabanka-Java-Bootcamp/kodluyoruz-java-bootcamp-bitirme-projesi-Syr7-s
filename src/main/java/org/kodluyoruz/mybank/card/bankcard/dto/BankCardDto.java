package org.kodluyoruz.mybank.card.bankcard.dto;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.card.bankcard.entity.BankCard;

import java.time.LocalDate;

@Data
@Builder
public class BankCardDto {
    private long bankCardAccountNumber;
    private String bankCardNameSurname;
    private int bankCardPassword;
    private LocalDate bankCardExpirationDate;
    private String securityCode;

    public BankCard toBankCard(){
        return BankCard.builder()
                .bankCardAccountNumber(this.bankCardAccountNumber)
                .bankCardNameSurname(this.bankCardNameSurname)
                .bankCardPassword(this.bankCardPassword)
                .bankCardExpirationDate(this.bankCardExpirationDate)
                .bankCardSecurityCode(this.securityCode)
                .build();
    }
}
