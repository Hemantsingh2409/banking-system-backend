package com.banking.controller;

import com.banking.dto.TransactionRequest;
import com.banking.model.Transaction;
import com.banking.repository.UserRepository;
import com.banking.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.transfer(request));
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody Map<String, Object> body) {
        String acc = (String) body.get("accountNumber");
        BigDecimal amt = new BigDecimal(body.get("amount").toString());
        return ResponseEntity.ok(transactionService.deposit(acc, amt));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody Map<String, Object> body) {
        String acc = (String) body.get("accountNumber");
        BigDecimal amt = new BigDecimal(body.get("amount").toString());
        return ResponseEntity.ok(transactionService.withdraw(acc, amt));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountId));
    }

    @GetMapping("/my-transactions")
    public ResponseEntity<List<Transaction>> getMyTransactions(Authentication auth) {
        Long userId = userRepository.findByEmail(auth.getName()).orElseThrow().getId();
        return ResponseEntity.ok(transactionService.getAllUserTransactions(userId));
    }
}
