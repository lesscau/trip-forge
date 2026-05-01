package com.lesscau.tripforge.transaction;

import com.lesscau.tripforge.account.Account;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ledger_transactions")
public class LedgerTransaction {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paid_by_account_id", nullable = false)
    private Account paidBy;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionParticipant> participants = new ArrayList<>();

    protected LedgerTransaction() {
    }

    public LedgerTransaction(UUID id, String title, BigDecimal amount, String currency, Account paidBy,
                             LocalDate transactionDate, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.currency = currency;
        this.paidBy = paidBy;
        this.transactionDate = transactionDate;
        this.createdAt = createdAt;
    }

    public void addParticipant(Account account, BigDecimal shareAmount) {
        participants.add(new TransactionParticipant(this, account, shareAmount));
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Account getPaidBy() {
        return paidBy;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<TransactionParticipant> getParticipants() {
        return Collections.unmodifiableList(participants);
    }
}
