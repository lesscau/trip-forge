package com.lesscau.tripforge;

import com.lesscau.tripforge.account.AccountController;
import com.lesscau.tripforge.transaction.TransactionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TripForgeApplicationIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void createsAccountAndTransaction() {
        ResponseEntity<AccountController.AccountResponse> accountResponse = restTemplate.postForEntity(
                "/api/accounts",
                new AccountController.CreateAccountRequest("Ola"),
                AccountController.AccountResponse.class
        );

        assertThat(accountResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(accountResponse.getBody()).isNotNull();
        UUID accountId = accountResponse.getBody().id();

        ResponseEntity<TransactionController.TransactionResponse> transactionResponse = restTemplate.postForEntity(
                "/api/transactions",
                new HttpEntity<>(new TransactionController.CreateTransactionRequest(
                        "Hotel deposit",
                        new BigDecimal("120.00"),
                        "EUR",
                        accountId,
                        LocalDate.of(2026, 5, 1),
                        List.of(accountId)
                )),
                TransactionController.TransactionResponse.class
        );

        assertThat(transactionResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(transactionResponse.getBody()).isNotNull();
        assertThat(transactionResponse.getBody().amount()).isEqualByComparingTo("120.00");
        assertThat(transactionResponse.getBody().participants()).hasSize(1);
    }
}
