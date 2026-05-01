package com.lesscau.tripforge;

import com.lesscau.tripforge.account.Account;
import com.lesscau.tripforge.account.AccountRepository;
import com.lesscau.tripforge.transaction.LedgerTransaction;
import com.lesscau.tripforge.transaction.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class TripForgeApplicationIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionService transactionService;

    @Test
    void createsAccountAndTransaction() {
        Account account = accountRepository.save(new Account(UUID.randomUUID(), "Ola", Instant.now()));

        LedgerTransaction transaction = transactionService.createTransaction(new TransactionService.CreateTransactionCommand(
                "Hotel deposit",
                new BigDecimal("120.00"),
                "EUR",
                account.getId(),
                LocalDate.of(2026, 5, 1),
                List.of(account.getId())
        ));

        assertThat(transaction.getId()).isNotNull();
        assertThat(transaction.getAmount()).isEqualByComparingTo("120.00");
        assertThat(transaction.getCurrency()).isEqualTo("EUR");
        assertThat(transaction.getParticipants()).hasSize(1);
        assertThat(transaction.getParticipants().getFirst().getShareAmount()).isEqualByComparingTo("120.00");
    }
}
