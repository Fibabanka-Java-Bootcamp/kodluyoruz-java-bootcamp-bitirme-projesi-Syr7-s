package org.kodluyoruz.mybank.creditcard.dto;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.customer.entity.Customer;

import java.time.LocalDate;

@Data
@Builder
public class CreditCardDto {
    private long cardNo;
    private String cardNameSurnname;
    private LocalDate expirationDate;
    private String securityCode;
    private int cardLimit;
    private int cardDebt;
    private Customer customer;

    public CreditCard toCreditCard(){
        return CreditCard.builder()
                .cardNO(this.cardNo)
                .cardNameSurname(this.cardNameSurnname)
                .expirationDate(this.expirationDate)
                .securityCode(this.securityCode)
                .cardLimit(this.cardLimit)
                .cardDebt(this.cardDebt)
                .customer(this.customer)
                .build();
    }
}
