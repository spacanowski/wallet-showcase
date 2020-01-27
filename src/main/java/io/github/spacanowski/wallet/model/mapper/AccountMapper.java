package io.github.spacanowski.wallet.model.mapper;

import io.github.spacanowski.wallet.model.data.Account;
import io.github.spacanowski.wallet.model.output.AccountOutput;

public class AccountMapper {

    public static AccountOutput toOutput(Account account) {
        return AccountOutput.builder()
                            .id(account.getId())
                            .balance(account.getBalance())
                            .build();
    }
}
