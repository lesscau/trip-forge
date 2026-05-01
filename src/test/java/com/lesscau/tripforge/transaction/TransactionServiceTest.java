package com.lesscau.tripforge.transaction;

import com.lesscau.tripforge.account.Account;
import com.lesscau.tripforge.account.AccountRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionServiceTest {

    private final LedgerTransactionRepository transactionRepository = mock(LedgerTransactionRepository.class);
    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final TransactionService transactionService = new TransactionService(transactionRepository, accountRepository);

    @Test
    void createsTransactionWithEqualParticipantShares() {
        UUID olaId = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();
        Account ola = new Account(olaId, "Ola", Instant.now());
        Account friend = new Account(friendId, "Friend", Instant.now());

        when(accountRepository.findById(olaId)).thenReturn(Optional.of(ola));
        when(accountRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(transactionRepository.save(any(LedgerTransaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LedgerTransaction transaction = transactionService.createTransaction(new TransactionService.CreateTransactionCommand(
                "Dinner",
                new BigDecimal("100.00"),
                "eur",
                olaId,
                LocalDate.of(2026, 5, 1),
                List.of(olaId, friendId)
        ));

        assertThat(transaction.getTitle()).isEqualTo("Dinner");
        assertThat(transaction.getCurrency()).isEqualTo("EUR");
        assertThat(transaction.getParticipants()).hasSize(2);
        assertThat(transaction.getParticipants())
                .extracting(TransactionParticipant::getShareAmount)
                .containsExactly(new BigDecimal("50.00"), new BigDecimal("50.00"));
    }

    @Test
    void rejectsNonPositiveAmount() {
        assertThatThrownBy(() -> transactionService.createTransaction(new TransactionService.CreateTransactionCommand(
                "Taxi",
                BigDecimal.ZERO,
                "EUR",
                UUID.randomUUID(),
                LocalDate.now(),
                List.of()
        ))).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("amount must be positive");
    }
}
