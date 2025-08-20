package com.example.bank_system.repository;

import com.example.bank_system.entity.Account;
import com.example.bank_system.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByCustomer(Customer customer);
    List<Account> findByCustomerId(Long customerId);
    boolean existsByAccountNumber(String accountNumber);
}
