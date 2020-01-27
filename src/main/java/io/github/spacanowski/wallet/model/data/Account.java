package io.github.spacanowski.wallet.model.data;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Data;

@Data
public class Account {

    private String id;
    private BigDecimal balance;
    private final ReadWriteLock readWriteLock;

    public Account() {
        readWriteLock = new ReentrantReadWriteLock();
    }

    public Lock readLock() {
        return readWriteLock.readLock();
    }

    public Lock writeLock() {
        return readWriteLock.writeLock();
    }
}
