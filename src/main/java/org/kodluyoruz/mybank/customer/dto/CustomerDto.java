package org.kodluyoruz.mybank.customer.dto;

import lombok.*;
import org.kodluyoruz.mybank.customer.entity.Customer;
import org.kodluyoruz.mybank.customer.enums.Gender;

import java.time.LocalDate;

@Data
@Builder
public class CustomerDto {
    private long customerID;
    private String customerTC;
    private String customerName;
    private String customerLastname;
    private Gender customerGender;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private boolean customerRemovable;
    private LocalDate customerBirthDate;

    public Customer toCustomer(){
        return Customer.builder()
                .customerID(this.customerID)
                .customerTC(this.customerTC)
                .customerName(this.customerName)
                .customerLastname(this.customerLastname)
                .customerGender(this.customerGender)
                .customerPhone(this.customerPhone)
                .customerEmail(this.customerEmail)
                .customerAddress(this.customerAddress)
                .customerRemovable(this.customerRemovable)
                .customerBirthDate(this.customerBirthDate)
                .build();

    }


}
