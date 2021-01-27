package org.kodluyoruz.mybank.account.demanddepositaccount.dto;

import lombok.*;
import org.kodluyoruz.mybank.account.demanddepositaccount.entity.DemandDepositAccount;
import org.kodluyoruz.mybank.customer.entity.Customer;

import java.time.LocalDate;


@Getter
@Setter
@Builder
public class DemandDepositAccountDto {
    private int demandDepositAccountIBAN;
    private int demandDepositAccountBalance;
    private String demandDepositAccountCurrency;
    private LocalDate demandDepositAccountCreationDate;
    private Customer customer;

    public DemandDepositAccount toDemandDepositAccount(){
        return DemandDepositAccount.builder()
                    .demandDepositAccountIBAN(this.demandDepositAccountIBAN)
                    .demandDepositAccountBalance(this.demandDepositAccountBalance)
                    .demandDepositAccountCurrency(this.demandDepositAccountCurrency)
                    .demandDepositAccountCreationDate(this.demandDepositAccountCreationDate)
                    .customer(customer)
                .build();

    }


}
