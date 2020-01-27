package io.github.spacanowski.wallet.model.output;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AccountOutput {

    private String id;
    private BigDecimal balance;
}
