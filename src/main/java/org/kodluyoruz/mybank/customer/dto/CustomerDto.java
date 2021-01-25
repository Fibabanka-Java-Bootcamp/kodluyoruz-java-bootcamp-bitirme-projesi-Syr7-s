package org.kodluyoruz.mybank.customer.dto;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.customer.entity.Customer;

import java.time.LocalDate;

@Data
@Builder
public class CustomerDto {
    private long customerID;
    private String customerTC;
    private String customerName;
    private String customerLastname;
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
                .customerPhone(this.customerPhone)
                .customerEmail(this.customerEmail)
                .customerAddress(this.customerAddress)
                .customerRemovable(this.customerRemovable)
                .customerBirthDate(this.customerBirthDate)
                .build();

    }

}
