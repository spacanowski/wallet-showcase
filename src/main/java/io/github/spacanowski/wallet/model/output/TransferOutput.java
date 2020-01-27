package io.github.spacanowski.wallet.model.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransferOutput {

    private AccountOutput from;
    private AccountOutput to;
}
