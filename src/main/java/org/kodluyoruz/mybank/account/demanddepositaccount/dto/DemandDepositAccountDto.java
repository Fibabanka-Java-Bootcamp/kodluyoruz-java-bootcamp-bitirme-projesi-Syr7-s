package org.kodluyoruz.mybank.account.demanddepositaccount.dto;

import lombok.*;
import org.kodluyoruz.mybank.account.demanddepositaccount.entity.DemandDepositAccount;
import org.kodluyoruz.mybank.bankcard.entity.BankCard;
import org.kodluyoruz.mybank.customer.entity.Customer;

import java.time.LocalDate;


@Data
@Builder
public class DemandDepositAccountDto {
    private long demandDepositAccountNumber;
    private String demandDepositAccountIBAN;
    private int demandDepositAccountBalance;
    private String demandDepositAccountCurrency;
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
