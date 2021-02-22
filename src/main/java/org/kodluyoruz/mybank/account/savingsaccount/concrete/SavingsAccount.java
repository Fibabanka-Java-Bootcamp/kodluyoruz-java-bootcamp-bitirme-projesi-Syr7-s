package org.kodluyoruz.mybank.account.savingsaccount.concrete;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCard;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.utilities.enums.currency.Currency;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingsAccount {
    @Id
    private long savingsAccountNumber;
    private String savingsAccountIBAN;
    private int savingsAccountBalance;
    @Enumerated(EnumType.STRING)
    private Currency savingsAccountCurrency;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate savingsAccountCreationDate;
    private int termTime;
    private double grossInterestReturn;
    private double savingsAccountNetGain;
    private double savingsAccountInterestRate;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "bankcard__no")
    private BankCard bankCard;

    public SavingsAccountDto toSavingsAccountDto() {
        return SavingsAccountDto.builder()
                .savingsAccountNumber(this.savingsAccountNumber)
                .savingsAccountIBAN(this.savingsAccountIBAN)
                .savingsAccountBalance(this.savingsAccountBalance)
                .savingsAccountCurrency(this.savingsAccountCurrency)
                .savingsAccountCreationDate(this.savingsAccountCreationDate)
                .termTime(this.termTime)
                .grossInterestReturn(this.grossInterestReturn)
                .savingsAccountNetGain(this.savingsAccountNetGain)
                .savingsAccountInterestRate(this.savingsAccountInterestRate)
                .customer(this.customer)
                .bankCard(this.bankCard)
                .build();
    }

}
