package com.banking.controller;

import com.banking.model.Account;
import com.banking.repository.UserRepository;
import com.banking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AccountController {
    private final AccountService accountService;
    private final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(Authentication auth, @RequestBody Map<String, String> body) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(accountService.createAccount(userId, body.get("accountType")));
    }

    @GetMapping("/my-accounts")
    public ResponseEntity<List<Account>> getMyAccounts(Authentication auth) {
        return ResponseEntity.ok(accountService.getUserAccounts(getUserId(auth)));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountByNumber(accountNumber));
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<?> blockAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.blockAccount(id));
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<?> activateAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.activateAccount(id));
    }

    private Long getUserId(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).orElseThrow().getId();
    }
}
