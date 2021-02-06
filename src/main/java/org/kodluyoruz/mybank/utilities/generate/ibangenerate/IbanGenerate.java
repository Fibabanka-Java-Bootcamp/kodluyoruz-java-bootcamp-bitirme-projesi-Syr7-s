package org.kodluyoruz.mybank.utilities.generate.ibangenerate;

import java.util.function.Function;

public class IbanGenerate {
    private IbanGenerate(){
    }
    public static final Function<String, String> generateIban = (accountNumber -> "TR30000571" + accountNumber);
}
