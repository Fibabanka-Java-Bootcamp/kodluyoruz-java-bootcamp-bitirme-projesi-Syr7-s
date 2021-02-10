package org.kodluyoruz.mybank.shopping.concrete;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.utilities.enums.currency.Currency;

import java.time.LocalDate;

@Data
@Builder
public class ShoppingDto {
    private int productID;
    private String productType;
    private String productName;
    private int productPrice;
    private Currency currency;
    private LocalDate productReceiveDate;
    private CreditCard creditCard;

    public Shopping toShopping() {
        return Shopping.builder()
                .productID(this.productID)
                .productType(this.productType)
                .productName(this.productName)
                .productPrice(this.productPrice)
                .currency(this.currency)
                .productReceiveDate(this.productReceiveDate)
                .creditCard(this.creditCard)
                .build();
    }
}
