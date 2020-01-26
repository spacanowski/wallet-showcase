package io.github.spacanowski.wallet.service;

import io.github.spacanowski.wallet.datastore.Wallet;
import io.github.spacanowski.wallet.model.Account;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class AccountService {

    private final Wallet wallet;

    public Account getAccount(String id) {
        return wallet.get(id);
    }
}
