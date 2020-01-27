package io.github.spacanowski.wallet.model.data;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class TransferOperation implements Operation {

    private final String from;
    private final String to;
    private final BigDecimal sum;

    @Override
    public Type getType() {
        return Operation.Type.TRANSFER;
    }
}
