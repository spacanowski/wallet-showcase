package io.github.spacanowski.wallet.model.data;

public interface Operation {

    Type getType();
    String getAudit();

    enum Type {

        CREATE,
        TRANSFER,
        DELETE
    }
}
