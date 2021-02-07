package org.kodluyoruz.mybank.utilities.generate.tcgenerate;

import java.util.Random;
import java.util.function.Supplier;

public final class TC {
    private TC() {
    }

    public static final Supplier<String> generateTC = () -> {
        StringBuilder generate = new StringBuilder();
        generate.append(1 + new Random().nextInt(9));
        for (int i = 0; i < 10; i++) {
            generate.append(new Random().nextInt(10));
        }
        return String.valueOf(generate);
    };
}
