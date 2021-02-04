package org.kodluyoruz.mybank.card.creditcard.dto;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.card.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.customer.entity.Customer;

import java.time.LocalDate;

@Data
@Builder
public class CreditCardDto {
    private long creditCardAccountNumber;
    private String cardNameSurname;
    private int cardPassword;
    private LocalDate expirationDate;
    private String securityCode;
    private int cardLimit;
    private int cardDebt;
    private String currency;
    private Customer customer;

    public CreditCard toCreditCard(){
        return CreditCard.builder()
                .cardAccountNumber(this.creditCardAccountNumber)
                .cardNameSurname(this.cardNameSurname)
                .cardPassword(this.cardPassword)
                .expirationDate(this.expirationDate)
                .securityCode(this.securityCode)
                .cardLimit(this.cardLimit)
                .cardDebt(this.cardDebt)
                .currency(this.currency)
                .customer(this.customer)
                .build();
    }
}
