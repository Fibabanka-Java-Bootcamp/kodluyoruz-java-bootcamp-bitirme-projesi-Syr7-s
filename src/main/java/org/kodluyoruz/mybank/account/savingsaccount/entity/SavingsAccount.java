package org.kodluyoruz.mybank.account.savingsaccount.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.account.savingsaccount.dto.SavingsAccountDto;
import org.kodluyoruz.mybank.bankcard.entity.BankCard;
import org.kodluyoruz.mybank.customer.entity.Customer;

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
    @GeneratedValue
    private int savingsAccountIBAN;
    private int savingsAccountBalance;
    private String savingsAccountCurrency;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate savingsAccountCreationDate;
    private double savingsAccountInterestRate;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="customer_id")
    private Customer customer;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "bankcard__no")
    private BankCard bankCard;
    public SavingsAccountDto toSavingsAccountDto(){
        return SavingsAccountDto.builder()
                .savingsAccountIBAN(this.savingsAccountIBAN)
                .savingsAccountBalance(this.savingsAccountBalance)
                .savingsAccountCurrency(this.savingsAccountCurrency)
                .savingsAccountCreationDate(this.savingsAccountCreationDate)
                .savingsAccountInterestRate(this.savingsAccountInterestRate)
                .customer(this.customer)
                .bankCard(this.bankCard)
                .build();
    }

}
