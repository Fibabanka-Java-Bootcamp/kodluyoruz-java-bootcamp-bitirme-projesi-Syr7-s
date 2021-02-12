package org.kodluyoruz.mybank.extractofaccount.concrete;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtractOfAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate accountCutOffTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDueTo;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "creditCardNo",referencedColumnName = "cardAccountNumber")
    private CreditCard creditCard;

    public ExtractOfAccountDto toExtractOfAccountDto(){
        return ExtractOfAccountDto.builder()
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
