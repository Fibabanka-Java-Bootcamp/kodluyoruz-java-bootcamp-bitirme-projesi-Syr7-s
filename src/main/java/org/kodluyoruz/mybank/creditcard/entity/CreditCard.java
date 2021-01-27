package org.kodluyoruz.mybank.creditcard.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.creditcard.dto.CreditCardDto;
import org.kodluyoruz.mybank.customer.entity.Customer;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreditCard {
    @Id
    @GeneratedValue
    private long cardNO;
    private String cardNameSurname;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate expirationDate;
    private String securityCode;
    private int cardLimit;
    private int cardDebt;
    @ManyToOne
    @JsonIgnore
    private Customer customer;

    public CreditCardDto toCreditCardDto(){
        return CreditCardDto.builder()
                .cardNo(this.cardNO)
                .cardNameSurnname(this.cardNameSurname)
                .expirationDate(this.expirationDate)
                .securityCode(this.securityCode)
                .cardLimit(this.cardLimit)
                .cardDebt(this.cardDebt)
                .customer(this.customer)
                .build();
    }
}
