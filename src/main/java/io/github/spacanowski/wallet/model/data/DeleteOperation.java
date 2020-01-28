package io.github.spacanowski.wallet.model.data;

import static java.lang.String.format;

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

    @Override
    public String getAudit() {
        return format("Deleted account '%s'", id);
    }
}
