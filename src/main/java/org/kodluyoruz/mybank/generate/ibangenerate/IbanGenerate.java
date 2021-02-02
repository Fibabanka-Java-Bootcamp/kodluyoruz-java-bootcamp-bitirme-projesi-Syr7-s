package org.kodluyoruz.mybank.generate.ibangenerate;

import java.util.function.Function;

public class IbanGenerate {
    public static Function<String, String> ibanGenerate = (accountNumber) -> {
        StringBuilder IBAN = new StringBuilder();
        IBAN.append("TR30000571").append(accountNumber);
        return IBAN.toString();
    };
}
