package org.kodluyoruz.mybank.utilities.debtprocess;

import org.kodluyoruz.mybank.card.creditcard.concrete.CreditCard;
import org.kodluyoruz.mybank.extractofaccount.concrete.ExtractOfAccount;

public class Debt {
    private Debt() {

    }

    public static void debtProcess(int payMoney, double minimumPayment, CreditCard creditCard, ExtractOfAccount extractOfAccount) {
        if (payMoney == 0) {
            extracted(minimumPayment, extractOfAccount);
            creditCard.setCardDebt((int) (creditCard.getCardDebt() + extractOfAccount.getTotalInterestAmount()));
            extractOfAccount.setTermDebt(creditCard.getCardDebt());
            extractOfAccount.setOldMinimumPaymentAmount(Math.abs(extractOfAccount.getMinimumPaymentAmount() - minimumPayment));
        } else {
            creditCard.setCardDebt((int) ((creditCard.getCardDebt()) - payMoney));
            extractOfAccount.setTermDebt(creditCard.getCardDebt());
            zeroSet(extractOfAccount);
        }
        extractOfAccount.setAccountCutOffTime(extractOfAccount.getPaymentDueTo().plusDays(20));
        extractOfAccount.setPaymentDueTo(extractOfAccount.getAccountCutOffTime().plusDays(10));
    }

    private static void zeroSet(ExtractOfAccount extractOfAccount) {
        extractOfAccount.setShoppingInterestAmount(0);
        extractOfAccount.setShoppingInterestAmountNext(0);
        extractOfAccount.setLateInterestAmount(0);
        extractOfAccount.setTotalInterestAmount(0);
        extractOfAccount.setOldMinimumPaymentAmount(0);
    }

    private static void extracted(double minimumPayment, ExtractOfAccount extractOfAccount) {
        double shoppingInterestAmount = ((extractOfAccount.getTermDebt() - minimumPayment) *
                extractOfAccount.getShoppingInterestRate() * (10 / 30.0)) / 100;
        double lateInterestAmount = ((extractOfAccount.getMinimumPaymentAmount() - minimumPayment) *
                extractOfAccount.getLateInterestRate() * (20 / 30.0)) / 100;
        double shoppingInterestAmountNext = ((extractOfAccount.getTermDebt() - extractOfAccount.getMinimumPaymentAmount()) *
                extractOfAccount.getShoppingInterestRate() * (20 / 30.0) / 100);
        extractOfAccount.setShoppingInterestAmount(shoppingInterestAmount);
        extractOfAccount.setLateInterestAmount(lateInterestAmount);
        extractOfAccount.setShoppingInterestAmountNext(shoppingInterestAmountNext);
        extractOfAccount.setTotalInterestAmount(shoppingInterestAmount + lateInterestAmount + shoppingInterestAmountNext);
    }
}
