package org.kodluyoruz.mybank.account.demanddepositaccount.concrete;

import lombok.*;
import org.kodluyoruz.mybank.card.bankcard.concrete.BankCard;
import org.kodluyoruz.mybank.customer.concrete.Customer;
import org.kodluyoruz.mybank.utilities.enums.currency.Currency;

import java.time.LocalDate;


@Data
@Builder
public class DemandDepositAccountDto {
    private long demandDepositAccountNumber;
    private String demandDepositAccountIBAN;
    private int demandDepositAccountBalance;
    private Currency demandDepositAccountCurrency;
    private LocalDate demandDepositAccountCreationDate;
    private Customer customer;
    private BankCard bankCard;

    public DemandDepositAccount toDemandDepositAccount() {
        return DemandDepositAccount.builder()
                .demandDepositAccountNumber(this.demandDepositAccountNumber)
                .demandDepositAccountIBAN(this.demandDepositAccountIBAN)
                .demandDepositAccountBalance(this.demandDepositAccountBalance)
                .demandDepositAccountCurrency(this.demandDepositAccountCurrency)
                .demandDepositAccountCreationDate(this.demandDepositAccountCreationDate)
                .customer(this.customer)
                .bankCard(this.bankCard)
                .build();

    }


}
