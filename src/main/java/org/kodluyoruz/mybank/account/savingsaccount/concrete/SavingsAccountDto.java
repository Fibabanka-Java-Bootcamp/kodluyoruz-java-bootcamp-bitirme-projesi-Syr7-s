package org.kodluyoruz.mybank.account.savingsaccount.concrete;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCard;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.utilities.enums.currency.Currency;

import java.time.LocalDate;

@Data
@Builder
public class SavingsAccountDto {
    private long savingsAccountNumber;
    private String savingsAccountIBAN;
    private int savingsAccountBalance;
    private Currency savingsAccountCurrency;
    private LocalDate savingsAccountCreationDate;
    private double savingsAccountInterestRate;
    private Customer customer;
    private BankCard bankCard;

    public SavingsAccount toSavingsAccount(){
        return SavingsAccount.builder()
                .savingsAccountNumber(this.savingsAccountNumber)
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
