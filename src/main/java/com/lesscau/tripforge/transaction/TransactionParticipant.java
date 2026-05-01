package com.lesscau.tripforge.transaction;

import com.lesscau.tripforge.account.Account;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transaction_participants")
@IdClass(TransactionParticipantId.class)
public class TransactionParticipant {

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private LedgerTransaction transaction;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal shareAmount;

    protected TransactionParticipant() {
    }

    public TransactionParticipant(LedgerTransaction transaction, Account account, BigDecimal shareAmount) {
        this.transaction = transaction;
        this.account = account;
        this.shareAmount = shareAmount;
    }

    public LedgerTransaction getTransaction() {
        return transaction;
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getShareAmount() {
        return shareAmount;
    }

    public UUID getAccountId() {
        return account.getId();
    }
}
