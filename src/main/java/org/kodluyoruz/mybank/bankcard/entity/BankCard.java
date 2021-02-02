package org.kodluyoruz.mybank.bankcard.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.account.demanddepositaccount.entity.DemandDepositAccount;
import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.kodluyoruz.mybank.bankcard.dto.BankCardDto;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BankCard {
    @Id
    private long bankCardAccountNumber;
    private String bankCardNameSurname;
    private int bankCardPassword;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bankCardExpirationDate;
    private String bankCardSecurityCode;


    @OneToMany(mappedBy = "bankCard")
    @JsonIgnore
    private Set<DemandDepositAccount> depositAccountBankCards;

    @OneToMany(mappedBy = "bankCard")
    @JsonIgnore
    private Set<SavingsAccount> savingsBankCards;

    public BankCardDto toBankCardDto() {
        return BankCardDto.builder()
                .bankCardAccountNumber(this.bankCardAccountNumber)
                .bankCardNameSurname(this.bankCardNameSurname)
                .bankCardPassword(this.bankCardPassword)
                .bankCardExpirationDate(this.bankCardExpirationDate)
                .securityCode(this.bankCardSecurityCode)
                .build();
    }

}
