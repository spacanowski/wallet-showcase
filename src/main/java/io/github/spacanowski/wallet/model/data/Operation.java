package io.github.spacanowski.wallet.model.data;

public interface Operation {

    Type getType();

    enum Type {

        CREATE,
        TRANSFER,
        DELETE
    }
}
