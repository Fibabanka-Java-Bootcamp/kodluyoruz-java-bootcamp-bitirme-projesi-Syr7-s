package org.kodluyoruz.mybank.extractofaccount.concrete;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;

import java.time.LocalDate;

@Data
@Builder
public class ExtractOfAccountDto {
    private int id;
    private double termDebt;
    private double oldDebt;
    private double minimumPaymentAmount;
    private double oldMinimumPaymentAmount;
    private double shoppingInterestRate;
    private double lateInterestRate;
    private double shoppingInterestAmount;
    private double shoppingInterestAmountNext;
    private double lateInterestAmount;
    private double totalInterestAmount;
    private LocalDate accountCutOffTime;
    private LocalDate paymentDueTo;
    private CreditCard creditCard;

    public ExtractOfAccount toExtractOfAccount() {
        return ExtractOfAccount.builder()
                .id(this.id)
                .termDebt(this.termDebt)
                .oldDebt(this.oldDebt)
                .minimumPaymentAmount(this.minimumPaymentAmount)
                .oldMinimumPaymentAmount(this.oldMinimumPaymentAmount)
                .shoppingInterestRate(this.shoppingInterestRate)
                .lateInterestRate(this.lateInterestRate)
                .shoppingInterestAmount(this.shoppingInterestAmount)
                .shoppingInterestAmountNext(this.shoppingInterestAmountNext)
                .lateInterestAmount(this.lateInterestAmount)
                .totalInterestAmount(this.totalInterestAmount)
                .accountCutOffTime(this.accountCutOffTime)
                .paymentDueTo(this.paymentDueTo)
                .creditCard(this.creditCard)
                .build();
    }
}
