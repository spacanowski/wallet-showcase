package io.github.spacanowski.wallet.exception;

import static java.lang.String.format;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String id) {
        super(format("No account with id %s found", id));
    }
}
