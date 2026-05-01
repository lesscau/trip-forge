package com.lesscau.tripforge.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public List<AccountResponse> listAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable UUID id) {
        return accountRepository.findById(id)
                .map(AccountResponse::from)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@RequestBody CreateAccountRequest request) {
        String name = request.normalizedName();
        if (name.isBlank()) {
            throw new IllegalArgumentException("Account name must not be blank");
        }
        Account account = new Account(UUID.randomUUID(), name, Instant.now());
        return AccountResponse.from(accountRepository.save(account));
    }

    public record CreateAccountRequest(String name) {
        String normalizedName() {
            return name == null ? "" : name.trim();
        }
    }

    public record AccountResponse(UUID id, String name, Instant createdAt) {
        static AccountResponse from(Account account) {
            return new AccountResponse(account.getId(), account.getName(), account.getCreatedAt());
        }
    }
}
