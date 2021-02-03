package org.kodluyoruz.mybank.extractofaccount.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.kodluyoruz.mybank.card.creditcard.entity.CreditCard;
import org.kodluyoruz.mybank.extractofaccount.dto.ExtractOfAccountDto;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate accountCutOffTime;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDueTo;
    private double bankRate;
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
                .accountCutOffTime(this.accountCutOffTime)
                .paymentDueTo(this.paymentDueTo)
                .bankRate(this.bankRate)
                .creditCard(this.creditCard)
                .build();
    }

}
