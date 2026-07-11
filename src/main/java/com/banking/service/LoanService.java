package com.banking.service;

import com.banking.dto.LoanRequest;
import com.banking.model.Loan;
import com.banking.model.User;
import com.banking.repository.LoanRepository;
import com.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public Loan applyLoan(Long userId, LoanRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found!"));

        BigDecimal emi = calculateEMI(
            request.getAmount(),
            request.getInterestRate(),
            request.getTenureMonths()
        );

        Loan loan = Loan.builder()
            .amount(request.getAmount())
            .interestRate(request.getInterestRate())
            .tenureMonths(request.getTenureMonths())
            .emi(emi)
            .loanType(Loan.LoanType.valueOf(request.getLoanType()))
            .status(Loan.LoanStatus.PENDING)
            .user(user)
            .build();

        return loanRepository.save(loan);
    }

    public Loan approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found!"));
        loan.setStatus(Loan.LoanStatus.APPROVED);
        loan.setApprovedAt(LocalDateTime.now());
        return loanRepository.save(loan);
    }

    public Loan rejectLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found!"));
        loan.setStatus(Loan.LoanStatus.REJECTED);
        return loanRepository.save(loan);
    }

    public List<Loan> getUserLoans(Long userId) {
        return loanRepository.findByUserId(userId);
    }

    public List<Loan> getAllPendingLoans() {
        return loanRepository.findByStatus(Loan.LoanStatus.PENDING);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    private BigDecimal calculateEMI(BigDecimal principal,
                                     Double annualRate,
                                     Integer months) {
        double monthlyRate = annualRate / 12 / 100;
        double emi = (principal.doubleValue() * monthlyRate
            * Math.pow(1 + monthlyRate, months))
            / (Math.pow(1 + monthlyRate, months) - 1);
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
    }
}
