package io.github.spacanowski.wallet.datastore;

import io.github.spacanowski.wallet.model.Account;

import java.util.concurrent.ConcurrentHashMap;

public class Wallet {

    private ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();

    public Account get(String id) {
        return accounts.get(id);
    }
}
