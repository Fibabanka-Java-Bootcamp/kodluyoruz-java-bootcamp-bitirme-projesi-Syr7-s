package org.kodluyoruz.mybank.customer.concrete;

import lombok.*;
import org.kodluyoruz.mybank.utilities.enums.gender.Gender;

import java.time.LocalDate;

@Data
@Builder
public class CustomerDto {
    private long customerTC;
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
