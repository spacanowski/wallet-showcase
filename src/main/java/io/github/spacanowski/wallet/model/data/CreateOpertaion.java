package io.github.spacanowski.wallet.model.data;

import static java.lang.String.format;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CreateOpertaion implements Operation {

    private final String id;
    private final BigDecimal initialBalance;

    @Override
    public Type getType() {
        return Operation.Type.CREATE;
    }

    @Override
    public String getAudit() {
        return format("Created account '%s' with balance '%s'", id, initialBalance);
    }
}
