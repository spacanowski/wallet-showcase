package io.github.spacanowski.wallet.model.input;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateAccount {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal balance;
}
