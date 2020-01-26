package io.github.spacanowski.wallet.model;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Account {

    private String id;
    private BigDecimal balance;
}
