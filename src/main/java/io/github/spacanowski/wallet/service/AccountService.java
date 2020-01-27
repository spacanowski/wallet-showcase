package io.github.spacanowski.wallet.service;

import static io.github.spacanowski.wallet.model.mapper.AccountMapper.toOutput;

import io.github.spacanowski.wallet.datastore.Wallet;
import io.github.spacanowski.wallet.exception.AccountNotFoundException;
import io.github.spacanowski.wallet.model.data.Account;
import io.github.spacanowski.wallet.model.input.CreateAccount;
import io.github.spacanowski.wallet.model.input.Transfer;
import io.github.spacanowski.wallet.model.output.AccountOutput;
import io.github.spacanowski.wallet.model.output.TransferOutput;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(onConstructor = @__({ @Inject }))
public class AccountService {

    private final Wallet wallet;

    public AccountOutput getAccount(String id) {
        var result = wallet.get(id);

        if (result == null) {
            throw new AccountNotFoundException(id);
        }

        return toAccountOutput(result);
    }

    public AccountOutput createAccount(CreateAccount account) {
        return toAccountOutput(wallet.create(account.getBalance()));
    }

    public TransferOutput transferResources(String from, String to, Transfer transfer) {
        if (from.equals(to)) {
            throw new IllegalArgumentException("Cannot transfer to same account");
        }

        wallet.transfer(from, to, transfer.getSum());

        return new TransferOutput(toAccountOutput(wallet.get(from)),
                                  toAccountOutput(wallet.get(to)));
    }

    public void deleteAccount(String id) {
        wallet.delete(id);
    }

    private AccountOutput toAccountOutput(Account account) {
        var readLock = account.readLock();
        readLock.lock();

        try {
            return toOutput(account);
        } finally {
            readLock.unlock();
        }
    }
}
