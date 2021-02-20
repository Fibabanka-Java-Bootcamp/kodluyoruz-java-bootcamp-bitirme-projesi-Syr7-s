package org.kodluyoruz.mybank.utilities.enums.messages;

public enum Messages {
    ;

    public enum Error {
        AN_ERROR_OCCURRED("An error occurred"),
        ACCOUNT_COULD_NOT_DELETED_BECAUSE_HAVE_MONEY_IN_YOUR_ACCOUNT("Account could not deleted.Because have money in your account."),
        ACCOUNT_COULD_NOT_FOUND("Account could not found."),
        ACCOUNT_NUMBER_AND_PASSWORD_COULD_NOT_MATCHED("Account number and password could not matched."),
        ACCOUNTS_COULD_SAME("Accounts could same."),
        CURRENCY_SHOULD_WRITE_UPPER_CASE("Currency should write upper case."),
        CARD_COULD_NOT_CREATED("Card could not created."),
        CARD_COULD_NOT_DELETED("Card could not deleted."),
        CARD_COULD_NOT_MATCHED_TO_YOUR_ACCOUNT("Card could not matched to your account."),
        CARD_PASSWORD_COULD_INCORRECT("Card password could incorrect."),
        CUSTOMER_COULD_NOT_DELETED("Customer could not deleted."),
        CUSTOMER_COULD_NOT_FOUND("Customer could not found."),
        CREDIT_CARD_LIMIT_OVER("Credit card limit over."),
        EXTRACT_OF_ACCOUNT_COULD_NOT_FOUND("Extract of account could not found."),
        NOT_ENOUGH_MONEY_IN_YOUR_ACCOUNT("Not enough money in your account."),
        PRODUCT_COULD_NOT_FOUND("Product could not found."),
        SERVER_ERROR("Server error.");

        public final String message;

        Error(String message) {
            this.message = message;
        }
    }

    public enum Info {
        NUMBERED_ACCOUNT_WAS_SUCCESSFULLY_DELETED(" numbered account was successfully deleted."),
        NAMED_CUSTOMER_CANCELED_BANK_CARD_USAGE(" named customer canceled bank card usage."),
        NAMED_CUSTOMER_REGISTER_TOTALLY_DELETED(" named customer register totally deleted.");

        public final String message;

        Info(String message) {
            this.message = message;
        }
    }
}
