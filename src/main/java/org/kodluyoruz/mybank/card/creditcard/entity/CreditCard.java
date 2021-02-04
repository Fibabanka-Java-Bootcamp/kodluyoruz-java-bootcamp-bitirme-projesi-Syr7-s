package org.kodluyoruz.mybank.card.creditcard.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.card.creditcard.dto.CreditCardDto;
import org.kodluyoruz.mybank.creditcardshopping.entity.Shopping;
import org.kodluyoruz.mybank.customer.entity.Customer;
import org.kodluyoruz.mybank.extractofaccount.entity.ExtractOfAccount;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreditCard {
    @Id
    private long cardAccountNumber;
    private String cardNameSurname;
    private int cardPassword;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate expirationDate;
    private String securityCode;
    private int cardLimit;
    private int cardDebt;
    private String currency;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Shopping> shoppings;

    @OneToOne(mappedBy = "creditCard")
    @JsonIgnore
    private ExtractOfAccount extractOfAccount;

    public CreditCardDto toCreditCardDto(){
        return CreditCardDto.builder()
                .creditCardAccountNumber(this.cardAccountNumber)
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
