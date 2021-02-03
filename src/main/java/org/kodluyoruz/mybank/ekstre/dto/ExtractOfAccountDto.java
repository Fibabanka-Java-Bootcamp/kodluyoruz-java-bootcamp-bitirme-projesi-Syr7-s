package org.kodluyoruz.mybank.ekstre.dto;

import lombok.Builder;
import lombok.Data;
import org.kodluyoruz.mybank.card.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.ekstre.entity.ExtractOfAccount;

import java.time.LocalDate;

@Data
@Builder
public class ExtractOfAccountDto {
    private int id;
    private double termDebt;
    private double oldDebt;
    private double minimumPaymentAmount;
    private LocalDate accountCutOffTime;
    private LocalDate paymentDueTo;
    private double bankRate;
    private CreditCard creditCard;

    public ExtractOfAccount toExtractOfAccount(){
        return ExtractOfAccount.builder()
                .id(this.id)
                .termDebt(this.termDebt)
                .oldDebt(this.oldDebt)
                .minimumPaymentAmount(this.minimumPaymentAmount)
                .accountCutOffTime(this.accountCutOffTime)
                .paymentDueTo(this.paymentDueTo)
                .bankRate(this.bankRate)
                .creditCard(this.creditCard)
                .build();
    }
}
