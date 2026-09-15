package com.andriiv.easybank.accounts.repository;

import com.andriiv.easybank.accounts.entity.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByCustomerId(Long customerId);
}
