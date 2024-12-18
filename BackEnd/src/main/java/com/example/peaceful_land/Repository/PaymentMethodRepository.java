package com.example.peaceful_land.Repository;

import com.example.peaceful_land.Entity.Account;
import com.example.peaceful_land.Entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    boolean existsByAccountEqualsAndNameEqualsAndAccountNumberEqualsAndHideEquals(Account account, String name, String accountNumber, boolean hide);
    List<PaymentMethod> findAllByAccountEqualsAndHideEquals(Account account, boolean hide);
}
