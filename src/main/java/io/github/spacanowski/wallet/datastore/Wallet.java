package io.github.spacanowski.wallet.datastore;

import io.github.spacanowski.wallet.model.data.Account;
import io.github.spacanowski.wallet.model.data.CreateOpertaion;
import io.github.spacanowski.wallet.model.data.Operation;

import java.math.BigDecimal;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Wallet {

    private ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();
    private Queue<Operation> operations = new ConcurrentLinkedQueue<>();

    public Account get(String id) {
        return accounts.get(id);
    }

    public Account create(BigDecimal initianlBalance) {
        var id = UUID.randomUUID().toString();
        var resault = new Account();

        resault.setId(id);
        resault.setBalance(initianlBalance);

        var old = accounts.putIfAbsent(id, resault);

        if (old != null) {
            log.info("Id collision during account creation. Retrying.");
            resault = create(initianlBalance);
        }

        operations.add(new CreateOpertaion(id, initianlBalance));

        return resault;
    }
}
