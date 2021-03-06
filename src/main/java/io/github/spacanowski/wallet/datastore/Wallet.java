package io.github.spacanowski.wallet.datastore;

import io.github.spacanowski.wallet.exception.AccountNotFoundException;
import io.github.spacanowski.wallet.exception.InsufficientResourcesException;
import io.github.spacanowski.wallet.model.data.Account;
import io.github.spacanowski.wallet.model.data.CreateOpertaion;
import io.github.spacanowski.wallet.model.data.DeleteOperation;
import io.github.spacanowski.wallet.model.data.Operation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Wallet {

    private final BiFunction<Account, BigDecimal, Boolean> subtract =
            (from, sum) -> executeSubtract(from, sum);

    private final BiFunction<Account, BigDecimal, Boolean> add =
            (to, sum) -> executeAdd(to, sum);

    private ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();
    private Queue<Operation> operations = new ConcurrentLinkedQueue<>();

    public Account get(String id) {
        var account = accounts.get(id);

        if (account == null) {
            return null;
        }

        var readLock = account.readLock();
        readLock.lock();

        try {
            var resault = new Account();

            resault.setId(account.getId());
            resault.setBalance(account.getBalance());

            return resault;
        } finally {
            readLock.unlock();
        }
    }

    public Account create(BigDecimal initianlBalance) {
        var id = UUID.randomUUID().toString();
        var account = new Account();

        account.setId(id);
        account.setBalance(initianlBalance);

        var old = accounts.putIfAbsent(id, account);

        if (old != null) {
            log.info("Id collision during account creation. Retrying.");
            account = create(initianlBalance);
        }

        operations.add(new CreateOpertaion(id, initianlBalance));

        var resault = new Account();

        resault.setId(account.getId());
        resault.setBalance(account.getBalance());

        return resault;
    }

    public void transfer(String fromId, String toId, BigDecimal sum) {
        var from = accounts.get(fromId);

        if (from == null) {
            throw new AccountNotFoundException(fromId);
        }

        var to = accounts.get(toId);

        if (to == null) {
            throw new AccountNotFoundException(toId);
        }

        // Subtract resources from 'from' account and add to 'to' account if subtract was successful
        if (subtract(from, sum) && !add(to, sum)) {
            // Rollback subtract of resources
            add(from, sum);
        }
    }

    public void delete(String id) {
        operations.add(new DeleteOperation(id));

        accounts.remove(id);
    }

    public List<String> getOperations() {
        return operations.stream()
                         .map(Operation::getAudit)
                         .collect(Collectors.toList());
    }

    private boolean subtract(Account from, BigDecimal sum) {
        return executeWithLock(from, sum, subtract);
    }

    private boolean executeSubtract(Account from, BigDecimal sum) {
        if (from.getBalance().compareTo(sum) < 0) {
            throw new InsufficientResourcesException(from.getId());
        }

        from.setBalance(from.getBalance().subtract(sum));

        return true;
    }

    private boolean add(Account to, BigDecimal sum) {
        return executeWithLock(to, sum, add);
    }

    private boolean executeAdd(Account to, BigDecimal sum) {
        to.setBalance(to.getBalance().add(sum));

        return true;
    }

    private boolean executeWithLock(Account account,
                                    BigDecimal sum,
                                    BiFunction<Account, BigDecimal, Boolean> operation) {
        log.debug("Locking for transfer account {}", account.getId());

        var lock = account.writeLock();
        lock.lock();

        log.debug("Locked for transfer account {}", account.getId());

        try {
            return operation.apply(account, sum);
        } catch (InsufficientResourcesException e) {
            log.error("Failed transfer on account {}", account.getId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Failed transfer on account {}", account.getId(), e);
            return false;
        } finally {
            log.debug("Unlocking after transfer account {}", account.getId());
            lock.unlock();
            log.debug("Unlocked after transfer account {}", account.getId());
        }
    }
}
