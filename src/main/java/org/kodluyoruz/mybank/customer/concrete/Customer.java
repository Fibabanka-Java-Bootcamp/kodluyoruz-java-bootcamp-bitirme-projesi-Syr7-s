package org.kodluyoruz.mybank.customer.concrete;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.account.demanddepositaccount.concrete.DemandDepositAccount;
import org.kodluyoruz.mybank.account.savingsaccount.concrete.SavingsAccount;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.utilities.enums.gender.Gender;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table
@Builder
public class Customer {
    @Id
    private long customerTC;
    private String customerName;
    private String customerLastname;
    @Enumerated(EnumType.STRING)
    private Gender customerGender;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
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
                .customerTC(this.customerTC)
                .customerName(this.customerName)
                .customerLastname(this.customerLastname)
                .customerGender(this.customerGender)
                .customerPhone(this.customerPhone)
                .customerEmail(this.customerEmail)
                .customerAddress(this.customerAddress)
                .customerBirthDate(this.customerBirthDate)
                .build();
    }

}
