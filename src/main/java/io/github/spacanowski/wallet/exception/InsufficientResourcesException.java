package io.github.spacanowski.wallet.exception;

import static java.lang.String.format;

public class InsufficientResourcesException extends RuntimeException {

    public InsufficientResourcesException(String id) {
        super(format("Account %s has insufficient resources to execute operation", id));
    }
}
