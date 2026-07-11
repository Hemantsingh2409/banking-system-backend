package com.banking.service;

import com.banking.model.Account;
import com.banking.model.User;
import com.banking.repository.AccountRepository;
import com.banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public Account createAccount(Long userId, String accountType) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found!"));

        Account account = Account.builder()
            .accountNumber(generateAccountNumber())
            .accountType(Account.AccountType.valueOf(accountType))
            .balance(BigDecimal.valueOf(1000.00))
            .status(Account.AccountStatus.ACTIVE)
            .user(user)
            .build();

        return accountRepository.save(account);
    }

    public List<Account> getUserAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found!"));
    }

    public Account blockAccount(Long id) {
        Account account = getAccountById(id);
        account.setStatus(Account.AccountStatus.BLOCKED);
        return accountRepository.save(account);
    }

    public Account activateAccount(Long id) {
        Account account = getAccountById(id);
        account.setStatus(Account.AccountStatus.ACTIVE);
        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        String number;
        Random random = new Random();
        do {
            number = String.valueOf(1000000000L + (long)(random.nextDouble() * 9000000000L));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}
