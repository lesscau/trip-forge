package com.lesscau.tripforge.transaction;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class TransactionParticipantId implements Serializable {

    private UUID transaction;
    private UUID account;

    public TransactionParticipantId() {
    }

    public TransactionParticipantId(UUID transaction, UUID account) {
        this.transaction = transaction;
        this.account = account;
    }

    public UUID getTransaction() {
        return transaction;
    }

    public UUID getAccount() {
        return account;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TransactionParticipantId that)) {
            return false;
        }
        return Objects.equals(transaction, that.transaction) && Objects.equals(account, that.account);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction, account);
    }
}
