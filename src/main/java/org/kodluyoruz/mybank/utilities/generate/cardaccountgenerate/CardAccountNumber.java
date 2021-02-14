package org.kodluyoruz.mybank.utilities.generate.cardaccountgenerate;


import java.util.Random;
import java.util.function.Supplier;

public final class CardAccountNumber {
    private CardAccountNumber() {
    }

    public static final Supplier<String> generateCardAccountNumber = () -> {
        StringBuilder cardAccountNumber = new StringBuilder();
        cardAccountNumber.append("85");
        for (int i = 0; i < 14; i++) {
            cardAccountNumber.append(new Random().nextInt(10));
        }
        return cardAccountNumber.toString();
    };
}
