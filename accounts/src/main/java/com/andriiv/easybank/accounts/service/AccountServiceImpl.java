package com.andriiv.easybank.accounts.service;

import com.andriiv.easybank.accounts.constants.AccountConstants;
import com.andriiv.easybank.accounts.dto.CustomerDto;
import com.andriiv.easybank.accounts.entity.Account;
import com.andriiv.easybank.accounts.entity.Customer;
import com.andriiv.easybank.accounts.exception.CustomerAlreadyExistsException;
import com.andriiv.easybank.accounts.mapper.CustomerMapper;
import com.andriiv.easybank.accounts.repository.AccountRepository;
import com.andriiv.easybank.accounts.repository.CustomerRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import lombok.AllArgsConstructor;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {
  private AccountRepository accountRepository;
  private CustomerRepository customerRepository;
  private Faker faker;

  /**
   * Creates a new account based on the provided customer details.
   *
   * @param customerDto - {@link CustomerDto} Object
   */
  @Override
  public void createAccount(CustomerDto customerDto) {
    Optional<Customer> optionalCustomer =
        customerRepository.findByMobileNumber(customerDto.getMobileNumber());
    if (optionalCustomer.isPresent()) {
      throw new CustomerAlreadyExistsException(
          "Customer with mobile number " + customerDto.getMobileNumber() + " already exists");
    }
    Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
    customer.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
    customer.setCreatedBy("Anonymous");
    Customer savedCustomer = customerRepository.save(customer);
    accountRepository.save(createNewAccountForCustomer(savedCustomer));
  }

  private Account createNewAccountForCustomer(Customer customer) {
    Account account = new Account();
    account.setCustomerId(customer.getCustomerId());
    account.setAccountNumber(Long.valueOf(faker.number().digits(10)));
    account.setAccountType(AccountConstants.SAVINGS);
    account.setBranchAddress(faker.address().fullAddress());
    account.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
    account.setCreatedBy("Anonymous");
    return account;
  }
}
