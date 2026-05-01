package com.lesscau.tripforge.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final LedgerTransactionRepository transactionRepository;
    private final TransactionService transactionService;

    public TransactionController(LedgerTransactionRepository transactionRepository, TransactionService transactionService) {
        this.transactionRepository = transactionRepository;
        this.transactionService = transactionService;
    }

    @GetMapping
    public List<TransactionResponse> listTransactions() {
        return transactionRepository.findAll().stream()
                .map(TransactionResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public TransactionResponse getTransaction(@PathVariable UUID id) {
        return transactionRepository.findById(id)
                .map(TransactionResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse createTransaction(@RequestBody CreateTransactionRequest request) {
        LedgerTransaction transaction = transactionService.createTransaction(request.toCommand());
        return TransactionResponse.from(transaction);
    }

    public record CreateTransactionRequest(
            String title,
            BigDecimal amount,
            String currency,
            UUID paidByAccountId,
            LocalDate transactionDate,
            List<UUID> participantAccountIds
    ) {
        TransactionService.CreateTransactionCommand toCommand() {
            return new TransactionService.CreateTransactionCommand(
                    title,
                    amount,
                    currency,
                    paidByAccountId,
                    transactionDate,
                    participantAccountIds
            );
        }
    }

    public record TransactionResponse(
            UUID id,
            String title,
            BigDecimal amount,
            String currency,
            UUID paidByAccountId,
            LocalDate transactionDate,
            Instant createdAt,
            List<ParticipantResponse> participants
    ) {
        static TransactionResponse from(LedgerTransaction transaction) {
            return new TransactionResponse(
                    transaction.getId(),
                    transaction.getTitle(),
                    transaction.getAmount(),
                    transaction.getCurrency(),
                    transaction.getPaidBy().getId(),
                    transaction.getTransactionDate(),
                    transaction.getCreatedAt(),
                    transaction.getParticipants().stream()
                            .map(participant -> new ParticipantResponse(participant.getAccountId(), participant.getShareAmount()))
                            .toList()
            );
        }
    }

    public record ParticipantResponse(UUID accountId, BigDecimal shareAmount) {
    }
}
