package com.banking.controller;

import com.banking.dto.LoanRequest;
import com.banking.model.Loan;
import com.banking.repository.UserRepository;
import com.banking.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class LoanController {
    private final LoanService loanService;
    private final UserRepository userRepository;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLoan(Authentication auth, @RequestBody LoanRequest request) {
        Long userId = userRepository.findByEmail(auth.getName()).orElseThrow().getId();
        return ResponseEntity.ok(loanService.applyLoan(userId, request));
    }

    @GetMapping("/my-loans")
    public ResponseEntity<List<Loan>> getMyLoans(Authentication auth) {
        Long userId = userRepository.findByEmail(auth.getName()).orElseThrow().getId();
        return ResponseEntity.ok(loanService.getUserLoans(userId));
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.approveLoan(id));
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.rejectLoan(id));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Loan>> getPendingLoans() {
        return ResponseEntity.ok(loanService.getAllPendingLoans());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }
}
