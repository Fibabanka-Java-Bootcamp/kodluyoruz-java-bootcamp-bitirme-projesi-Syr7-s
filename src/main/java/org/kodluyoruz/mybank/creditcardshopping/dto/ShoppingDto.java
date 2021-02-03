package org.kodluyoruz.mybank.creditcardshopping.dto;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.card.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.creditcardshopping.entity.Shopping;

import java.time.LocalDate;

@Data
@Builder
public class ShoppingDto {
    private int productID;
    private String productType;
    private String productName;
    private int productPrice;
    private LocalDate productReceiveDate;
    private CreditCard creditCard;

    public Shopping toShopping(){
        return Shopping.builder()
                .productID(this.productID)
                .productType(this.productType)
                .productName(this.productName)
                .productPrice(this.productPrice)
                .productReceiveDate(this.productReceiveDate)
                .creditCard(this.creditCard)
                .build();
    }
}
