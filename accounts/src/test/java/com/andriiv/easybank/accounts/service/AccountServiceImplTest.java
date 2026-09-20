package com.andriiv.easybank.accounts.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.andriiv.easybank.accounts.constants.AccountConstants;
import com.andriiv.easybank.accounts.dto.AccountDto;
import com.andriiv.easybank.accounts.dto.CustomerDto;
import com.andriiv.easybank.accounts.entity.Account;
import com.andriiv.easybank.accounts.entity.Customer;
import com.andriiv.easybank.accounts.exception.CustomerAlreadyExistsException;
import com.andriiv.easybank.accounts.exception.ResourceNotFoundException;
import com.andriiv.easybank.accounts.repository.AccountRepository;
import com.andriiv.easybank.accounts.repository.CustomerRepository;
import java.util.Optional;
import net.datafaker.Faker;
import net.datafaker.providers.base.Address;
import net.datafaker.providers.base.Number;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

  @Mock private AccountRepository accountRepository;
  @Mock private CustomerRepository customerRepository;
  @Mock private Faker faker;
  @Mock private Number fakerNumber;
  @Mock private Address fakerAddress;

  @InjectMocks private AccountServiceImpl accountService;

  @Test
  void createAccount_whenCustomerDoesNotExist_savesCustomerAndAccount() {
    CustomerDto customerDto = customerDto("Jane Doe", "jane.doe@example.com", "9876543210");

    when(customerRepository.findByMobileNumber(customerDto.getMobileNumber()))
        .thenReturn(Optional.empty());
    when(customerRepository.save(any(Customer.class)))
        .thenAnswer(
            invocation -> {
              Customer customer = invocation.getArgument(0);
              customer.setCustomerId(10L);
              return customer;
            });
    when(faker.number()).thenReturn(fakerNumber);
    when(faker.address()).thenReturn(fakerAddress);
    when(fakerNumber.digits(10)).thenReturn("1234567890");
    when(fakerAddress.fullAddress()).thenReturn("10 Main Street, Warsaw");

    accountService.createAccount(customerDto);

    ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
    ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
    verify(customerRepository).save(customerCaptor.capture());
    verify(accountRepository).save(accountCaptor.capture());

    Customer savedCustomer = customerCaptor.getValue();
    Account savedAccount = accountCaptor.getValue();
    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(savedCustomer.getName()).isEqualTo("Jane Doe");
    softly.assertThat(savedCustomer.getEmail()).isEqualTo("jane.doe@example.com");
    softly.assertThat(savedCustomer.getMobileNumber()).isEqualTo("9876543210");
    softly.assertThat(savedCustomer.getCreatedAt()).isNotNull();
    softly.assertThat(savedCustomer.getCreatedBy()).isEqualTo("Anonymous");
    softly.assertThat(savedAccount.getCustomerId()).isEqualTo(10L);
    softly.assertThat(savedAccount.getAccountNumber()).isEqualTo(1234567890L);
    softly.assertThat(savedAccount.getAccountType()).isEqualTo(AccountConstants.SAVINGS);
    softly.assertThat(savedAccount.getBranchAddress()).isEqualTo("10 Main Street, Warsaw");
    softly.assertThat(savedAccount.getCreatedAt()).isNotNull();
    softly.assertThat(savedAccount.getCreatedBy()).isEqualTo("Application");
    softly.assertAll();
  }

  @Test
  void createAccount_whenCustomerAlreadyExists_throwsExceptionAndDoesNotSave() {
    CustomerDto customerDto = customerDto("Jane Doe", "jane.doe@example.com", "9876543210");
    Customer existingCustomer = new Customer();

    when(customerRepository.findByMobileNumber(customerDto.getMobileNumber()))
        .thenReturn(Optional.of(existingCustomer));

    assertThatThrownBy(() -> accountService.createAccount(customerDto))
        .isInstanceOf(CustomerAlreadyExistsException.class)
        .hasMessageContaining("Customer with mobile number 9876543210 already exists");

    verify(customerRepository, never()).save(any());
    verify(accountRepository, never()).save(any());
  }

  @Test
  void fetchAccountDetails_whenCustomerAndAccountExist_returnsCombinedDetails() {
    Customer customer = customer(10L, "Jane Doe", "jane.doe@example.com", "9876543210");
    Account account = account(10L, 1234567890L, "Savings", "10 Main Street, Warsaw");

    when(customerRepository.findByMobileNumber("9876543210")).thenReturn(Optional.of(customer));
    when(accountRepository.findByCustomerId(10L)).thenReturn(Optional.of(account));

    CustomerDto result = accountService.fetchAccountDetails("9876543210");

    assertThat(result.getName()).isEqualTo("Jane Doe");
    assertThat(result.getEmail()).isEqualTo("jane.doe@example.com");
    assertThat(result.getMobileNumber()).isEqualTo("9876543210");
    assertThat(result.getAccount()).isNotNull();
    assertThat(result.getAccount().getAccountNumber()).isEqualTo(1234567890L);
    assertThat(result.getAccount().getAccountType()).isEqualTo("Savings");
    assertThat(result.getAccount().getBranchAddress()).isEqualTo("10 Main Street, Warsaw");
  }

  @Test
  void fetchAccountDetails_whenCustomerDoesNotExist_throwsException() {
    when(customerRepository.findByMobileNumber("9876543210")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> accountService.fetchAccountDetails("9876543210"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining(
            "Customer not found with the given input data mobileNumber:9876543210");

    verify(accountRepository, never()).findByCustomerId(any());
  }

  @Test
  void fetchAccountDetails_whenAccountDoesNotExist_throwsException() {
    Customer customer = customer(10L, "Jane Doe", "jane.doe@example.com", "9876543210");
    when(customerRepository.findByMobileNumber("9876543210")).thenReturn(Optional.of(customer));
    when(accountRepository.findByCustomerId(10L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> accountService.fetchAccountDetails("9876543210"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Account not found with the given input data customerId:10");
  }

  @Test
  void fetchAllAccountsDetails_whenCustomersExist_returnsMappedDetails() {
    Customer firstCustomer = customer(1L, "Jane Doe", "jane@example.com", "1111111111");
    Customer secondCustomer = customer(2L, "John Doe", "john@example.com", "2222222222");
    Account firstAccount = account(1L, 1000000001L, "Savings", "Warsaw");
    Account secondAccount = account(2L, 1000000002L, "Current", "Krakow");

    when(customerRepository.findAll()).thenReturn(java.util.List.of(firstCustomer, secondCustomer));
    when(accountRepository.findByCustomerId(1L)).thenReturn(Optional.of(firstAccount));
    when(accountRepository.findByCustomerId(2L)).thenReturn(Optional.of(secondAccount));

    var result = accountService.fetchAllAccountsDetails();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getName()).isEqualTo("Jane Doe");
    assertThat(result.get(0).getAccount().getAccountNumber()).isEqualTo(1000000001L);
    assertThat(result.get(1).getName()).isEqualTo("John Doe");
    assertThat(result.get(1).getAccount().getAccountType()).isEqualTo("Current");
    verify(accountRepository).findByCustomerId(1L);
    verify(accountRepository).findByCustomerId(2L);
  }

  private CustomerDto customerDto(String name, String email, String mobileNumber) {
    CustomerDto customerDto = new CustomerDto();
    customerDto.setName(name);
    customerDto.setEmail(email);
    customerDto.setMobileNumber(mobileNumber);
    return customerDto;
  }

  private Customer customer(Long customerId, String name, String email, String mobileNumber) {
    Customer customer = new Customer();
    customer.setCustomerId(customerId);
    customer.setName(name);
    customer.setEmail(email);
    customer.setMobileNumber(mobileNumber);
    return customer;
  }

  private Account account(
      Long customerId, Long accountNumber, String accountType, String branchAddress) {
    Account account = new Account();
    account.setCustomerId(customerId);
    account.setAccountNumber(accountNumber);
    account.setAccountType(accountType);
    account.setBranchAddress(branchAddress);
    return account;
  }

  @Test
  void updateAccount_whenSuccess_updatesAndReturnsTrue() {
    CustomerDto customerDto = new CustomerDto();
    customerDto.setName("Jane Doe");
    customerDto.setEmail("jane.doe@example.com");
    customerDto.setMobileNumber("9876543210");

    AccountDto accountDto = new AccountDto();
    accountDto.setAccountNumber(1000000001L);
    accountDto.setAccountType("Current");
    accountDto.setBranchAddress("456 Oak Avenue");
    customerDto.setAccount(accountDto);

    Account existingAccount = new Account();
    existingAccount.setAccountNumber(1000000001L);
    existingAccount.setCustomerId(10L);
    existingAccount.setAccountType("Savings");
    existingAccount.setBranchAddress("123 Main Street");

    Customer existingCustomer = new Customer();
    existingCustomer.setCustomerId(10L);
    existingCustomer.setName("John Doe");
    existingCustomer.setEmail("john.doe@example.com");
    existingCustomer.setMobileNumber("1234567890");

    when(accountRepository.findById(1000000001L)).thenReturn(Optional.of(existingAccount));
    when(accountRepository.save(existingAccount)).thenReturn(existingAccount);
    when(customerRepository.findById(10L)).thenReturn(Optional.of(existingCustomer));
    when(customerRepository.save(existingCustomer)).thenReturn(existingCustomer);

    boolean isUpdated = accountService.updateCustomerAccountDetails(customerDto);

    assertThat(isUpdated).isTrue();
    assertThat(existingAccount.getAccountType()).isEqualTo("Current");
    assertThat(existingAccount.getBranchAddress()).isEqualTo("456 Oak Avenue");
    assertThat(existingCustomer.getName()).isEqualTo("Jane Doe");
    assertThat(existingCustomer.getEmail()).isEqualTo("jane.doe@example.com");
    assertThat(existingCustomer.getMobileNumber()).isEqualTo("9876543210");

    verify(accountRepository).save(existingAccount);
    verify(customerRepository).save(existingCustomer);
  }

  @Test
  void updateAccount_whenAccountNotFound_throwsResourceNotFoundException() {
    CustomerDto customerDto = new CustomerDto();
    AccountDto accountDto = new AccountDto();
    accountDto.setAccountNumber(9999999999L);
    customerDto.setAccount(accountDto);

    when(accountRepository.findById(9999999999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> accountService.updateCustomerAccountDetails(customerDto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining(
            "Account not found with the given input data accountNumber:9999999999");

    verify(customerRepository, never()).save(any());
  }

  @Test
  void updateAccount_whenCustomerNotFound_throwsResourceNotFoundException() {
    CustomerDto customerDto = new CustomerDto();
    AccountDto accountDto = new AccountDto();
    accountDto.setAccountNumber(1000000001L);
    customerDto.setAccount(accountDto);

    Account existingAccount = new Account();
    existingAccount.setAccountNumber(1000000001L);
    existingAccount.setCustomerId(20L);

    when(accountRepository.findById(1000000001L)).thenReturn(Optional.of(existingAccount));
    when(accountRepository.save(existingAccount)).thenReturn(existingAccount);
    when(customerRepository.findById(20L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> accountService.updateCustomerAccountDetails(customerDto))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Customer not found with the given input data customerId:20");

    verify(customerRepository, never()).save(any());
  }

  @Test
  void updateAccount_whenCustomerDtoIsNull_returnsFalse() {
    boolean isUpdated = accountService.updateCustomerAccountDetails(null);

    assertThat(isUpdated).isFalse();
    verify(accountRepository, never()).findById(any());
  }

  @Test
  void updateAccount_whenAccountDtoIsNull_returnsFalse() {
    CustomerDto customerDto = new CustomerDto();
    customerDto.setAccount(null);

    boolean isUpdated = accountService.updateCustomerAccountDetails(customerDto);

    assertThat(isUpdated).isFalse();
    verify(accountRepository, never()).findById(any());
  }

  @Test
  void updateAccount_whenAccountNumberIsNull_returnsFalse() {
    CustomerDto customerDto = new CustomerDto();
    AccountDto accountDto = new AccountDto();
    accountDto.setAccountNumber(null);
    customerDto.setAccount(accountDto);

    boolean isUpdated = accountService.updateCustomerAccountDetails(customerDto);

    assertThat(isUpdated).isFalse();
    verify(accountRepository, never()).findById(any());
  }
}
