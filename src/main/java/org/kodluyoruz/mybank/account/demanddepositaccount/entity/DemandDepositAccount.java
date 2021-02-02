package org.kodluyoruz.mybank.account.demanddepositaccount.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.account.demanddepositaccount.dto.DemandDepositAccountDto;
import org.kodluyoruz.mybank.bankcard.entity.BankCard;
import org.kodluyoruz.mybank.customer.entity.Customer;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandDepositAccount {
    @Id
    private long demandDepositAccountNumber;
    private String demandDepositAccountIBAN;
    private int demandDepositAccountBalance;
    private String demandDepositAccountCurrency;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate demandDepositAccountCreationDate;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "bankcard_no")
    private BankCard bankCard;

    public DemandDepositAccountDto toDemandDepositAccountDto() {
        return DemandDepositAccountDto.builder()
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
