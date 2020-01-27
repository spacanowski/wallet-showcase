package io.github.spacanowski.wallet.model.data;

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
}
