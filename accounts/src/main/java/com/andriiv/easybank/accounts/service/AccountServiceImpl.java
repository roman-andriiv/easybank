package com.andriiv.easybank.accounts.service;

import com.andriiv.easybank.accounts.constants.AccountConstants;
import com.andriiv.easybank.accounts.dto.AccountDto;
import com.andriiv.easybank.accounts.dto.CustomerDto;
import com.andriiv.easybank.accounts.entity.Account;
import com.andriiv.easybank.accounts.entity.Customer;
import com.andriiv.easybank.accounts.exception.CustomerAlreadyExistsException;
import com.andriiv.easybank.accounts.exception.ResourceNotFoundException;
import com.andriiv.easybank.accounts.mapper.AccountsMapper;
import com.andriiv.easybank.accounts.mapper.CustomerMapper;
import com.andriiv.easybank.accounts.repository.AccountRepository;
import com.andriiv.easybank.accounts.repository.CustomerRepository;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
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

  /**
   * Fetches account details for the customer identified by the given mobile number.
   *
   * @param mobileNumber the customer's registered mobile number
   * @return the customer's account details
   * @throws ResourceNotFoundException when no customer matches the mobile number
   */
  @Override
  public CustomerDto fetchAccountDetails(String mobileNumber) {
    Customer customer =
        customerRepository
            .findByMobileNumber(mobileNumber)
            .orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
    return mapCustomerWithAccount(customer);
  }

  /**
   * Fetches account details for all customers.
   *
   * @return a list of customer account details
   * @throws ResourceNotFoundException when a customer has no associated account
   */
  @Override
  public List<CustomerDto> fetchAllAccountsDetails() {
    return customerRepository.findAll().stream().map(this::mapCustomerWithAccount).toList();
  }

  private CustomerDto mapCustomerWithAccount(Customer customer) {
    Account account =
        accountRepository
            .findByCustomerId(customer.getCustomerId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Account", "customerId", customer.getCustomerId().toString()));
    CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
    customerDto.setAccount(AccountsMapper.mapToAccountsDto(account, new AccountDto()));
    return customerDto;
  }

  private Account createNewAccountForCustomer(Customer customer) {
    Account account = new Account();
    account.setCustomerId(customer.getCustomerId());
    account.setAccountNumber(Long.valueOf(faker.number().digits(10)));
    account.setAccountType(AccountConstants.SAVINGS);
    account.setBranchAddress(faker.address().fullAddress());
    account.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
    account.setCreatedBy("Application");
    return account;
  }
}
