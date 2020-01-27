package io.github.spacanowski.wallet.model.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DeleteOperation implements Operation {

    private final String id;

    @Override
    public Type getType() {
        return Operation.Type.DELETE;
    }
}
