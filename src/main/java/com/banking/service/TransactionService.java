package com.banking.service;

import com.banking.dto.TransactionRequest;
import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.repository.AccountRepository;
import com.banking.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Transaction transfer(TransactionRequest request) {
        Account fromAccount = accountRepository
            .findByAccountNumber(request.getFromAccountNumber())
            .orElseThrow(() -> new RuntimeException("Source account not found!"));

        Account toAccount = accountRepository
            .findByAccountNumber(request.getToAccountNumber())
            .orElseThrow(() -> new RuntimeException("Destination account not found!"));

        if (fromAccount.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Source account is not active!");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance!");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(request.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(request.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = Transaction.builder()
            .amount(request.getAmount())
            .type(Transaction.TransactionType.TRANSFER)
            .status(Transaction.TransactionStatus.SUCCESS)
            .description(request.getDescription() != null ?
                request.getDescription() : "Fund Transfer")
            .referenceNumber(UUID.randomUUID().toString())
            .account(fromAccount)
            .toAccountNumber(request.getToAccountNumber())
            .build();

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction deposit(String accountNumber, BigDecimal amount) {
        Account account = accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found!"));

        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active!");
        }

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
            .amount(amount)
            .type(Transaction.TransactionType.CREDIT)
            .status(Transaction.TransactionStatus.SUCCESS)
            .description("Deposit")
            .referenceNumber(UUID.randomUUID().toString())
            .account(account)
            .build();

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdraw(String accountNumber, BigDecimal amount) {
        Account account = accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found!"));

        if (account.getStatus() != Account.AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active!");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance!");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
            .amount(amount)
            .type(Transaction.TransactionType.DEBIT)
            .status(Transaction.TransactionStatus.SUCCESS)
            .description("Withdrawal")
            .referenceNumber(UUID.randomUUID().toString())
            .account(account)
            .build();

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    public List<Transaction> getAllUserTransactions(Long userId) {
        return transactionRepository.findAllByUserId(userId);
    }
}
