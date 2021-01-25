package org.kodluyoruz.mybank.customer.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.kodluyoruz.mybank.customer.dto.CustomerDto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

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
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate customerBirthDate;

    public CustomerDto toCustomerDto(){
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
