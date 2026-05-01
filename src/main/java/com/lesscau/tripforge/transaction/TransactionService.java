package com.lesscau.tripforge.transaction;

import com.lesscau.tripforge.account.Account;
import com.lesscau.tripforge.account.AccountNotFoundException;
import com.lesscau.tripforge.account.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class TransactionService {

    private final LedgerTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(LedgerTransactionRepository transactionRepository, AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public LedgerTransaction createTransaction(CreateTransactionCommand command) {
        validate(command);

        Account paidBy = accountRepository.findById(command.paidByAccountId())
                .orElseThrow(() -> new AccountNotFoundException(command.paidByAccountId()));

        LedgerTransaction transaction = new LedgerTransaction(
                UUID.randomUUID(),
                command.title().trim(),
                command.amount().setScale(2, RoundingMode.HALF_UP),
                command.currency().trim().toUpperCase(Locale.ROOT),
                paidBy,
                command.transactionDate(),
                Instant.now()
        );

        List<UUID> participantIds = command.participantAccountIds().isEmpty()
                ? List.of(command.paidByAccountId())
                : command.participantAccountIds();

        BigDecimal share = transaction.getAmount()
                .divide(BigDecimal.valueOf(participantIds.size()), 2, RoundingMode.HALF_UP);

        List<Account> participants = new ArrayList<>();
        for (UUID accountId : participantIds) {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException(accountId));
            participants.add(account);
        }

        for (Account participant : participants) {
            transaction.addParticipant(participant, share);
        }

        return transactionRepository.save(transaction);
    }

    private static void validate(CreateTransactionCommand command) {
        if (command.title() == null || command.title().trim().isBlank()) {
            throw new IllegalArgumentException("Transaction title must not be blank");
        }
        if (command.amount() == null || command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        if (command.currency() == null || !command.currency().trim().matches("[A-Za-z]{3}")) {
            throw new IllegalArgumentException("Currency must be a three-letter ISO code");
        }
        if (command.paidByAccountId() == null) {
            throw new IllegalArgumentException("Paid by account id must be provided");
        }
        if (command.transactionDate() == null) {
            throw new IllegalArgumentException("Transaction date must be provided");
        }
    }

    public record CreateTransactionCommand(
            String title,
            BigDecimal amount,
            String currency,
            UUID paidByAccountId,
            LocalDate transactionDate,
            List<UUID> participantAccountIds
    ) {
        public CreateTransactionCommand {
            participantAccountIds = participantAccountIds == null ? List.of() : List.copyOf(participantAccountIds);
        }
    }
}
