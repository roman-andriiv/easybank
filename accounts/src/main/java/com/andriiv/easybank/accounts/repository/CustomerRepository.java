package com.andriiv.easybank.accounts.repository;

import com.andriiv.easybank.accounts.entity.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

  Optional<Customer> findByMobileNumber(String mobileNumber);
}
