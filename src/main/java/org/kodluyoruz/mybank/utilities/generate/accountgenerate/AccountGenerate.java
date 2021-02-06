package org.kodluyoruz.mybank.utilities.generate.accountgenerate;

import java.util.Random;
import java.util.function.Supplier;

public class AccountGenerate {
    private AccountGenerate() {
    }

    public static final Supplier<String> generateAccount = () -> {
        StringBuilder accountNumber = new StringBuilder();
        accountNumber.append("8500");
        for (int i = 0; i < 12; i++) {
            accountNumber.append(new Random().nextInt(10));
        }
        return accountNumber.toString();
    };
}
