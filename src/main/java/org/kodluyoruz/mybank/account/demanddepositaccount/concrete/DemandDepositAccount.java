package org.kodluyoruz.mybank.account.demanddepositaccount.concrete;

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
public class DemandDepositAccount {
    @Id
    private long demandDepositAccountNumber;
    private String demandDepositAccountIBAN;
    private int demandDepositAccountBalance;
    @Enumerated(EnumType.STRING)
    private Currency demandDepositAccountCurrency;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate demandDepositAccountCreationDate;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(cascade = CascadeType.ALL)
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
