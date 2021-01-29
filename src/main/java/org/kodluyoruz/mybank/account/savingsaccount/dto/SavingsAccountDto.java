package org.kodluyoruz.mybank.account.savingsaccount.dto;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.kodluyoruz.mybank.customer.entity.Customer;

import java.time.LocalDate;

@Data
@Builder
public class SavingsAccountDto {
    private int savingsAccountIBAN;
    private int savingsAccountBalance;
    private String savingsAccountCurrency;
    private LocalDate savingsAccountCreationDate;
    private double savingsAccountInterestRate;
    private Customer customer;

    public SavingsAccount toSavingsAccount(){
        return SavingsAccount.builder()
                .savingsAccountIBAN(this.savingsAccountIBAN)
                .savingsAccountBalance(this.savingsAccountBalance)
                .savingsAccountCurrency(this.savingsAccountCurrency)
                .savingsAccountCreationDate(this.savingsAccountCreationDate)
                .savingsAccountInterestRate(this.savingsAccountInterestRate)
                .customer(this.customer)
                .build();
    }
}
