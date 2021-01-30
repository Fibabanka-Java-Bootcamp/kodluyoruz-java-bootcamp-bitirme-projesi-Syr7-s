package org.kodluyoruz.mybank.customer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.account.demanddepositaccount.entity.DemandDepositAccount;
import org.kodluyoruz.mybank.account.savingsaccount.entity.SavingsAccount;
import org.kodluyoruz.mybank.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Builder
public class Customer {
    @Id
    @GeneratedValue
    private long customerID;
    private String customerTC;
    private String customerName;
    private String customerLastname;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private boolean customerRemovable;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate customerBirthDate;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<DemandDepositAccount> demandDepositAccounts;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<CreditCard> creditCards;

    @OneToMany(mappedBy = "customer",cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<SavingsAccount> savingsAccounts;
    public CustomerDto toCustomerDto() {
        return CustomerDto.builder()
                .customerID(this.customerID)
                .customerTC(this.customerTC)
                .customerName(this.customerName)
                .customerLastname(this.customerLastname)
                .customerPhone(this.customerPhone)
                .customerEmail(this.customerEmail)
                .customerAddress(this.customerAddress)
                .customerRemovable(this.customerRemovable)
                .customerBirthDate(this.customerBirthDate)
                .build();
    }

}
