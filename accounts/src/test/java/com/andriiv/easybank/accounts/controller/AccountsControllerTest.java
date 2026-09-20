package com.andriiv.easybank.accounts.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.andriiv.easybank.accounts.constants.AccountConstants;
import com.andriiv.easybank.accounts.dto.AccountDto;
import com.andriiv.easybank.accounts.dto.CustomerDto;
import com.andriiv.easybank.accounts.exception.CustomerAlreadyExistsException;
import com.andriiv.easybank.accounts.exception.GlobalExceptionHandler;
import com.andriiv.easybank.accounts.exception.ResourceNotFoundException;
import com.andriiv.easybank.accounts.service.AccountService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AccountsControllerTest {

  private MockMvc mockMvc;

  @Mock private AccountService accountService;

  @InjectMocks private AccountsController accountsController;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(accountsController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void createAccount_whenSuccess_returns201Created() throws Exception {
    String requestJson =
        """
        {
          "name": "Jane Doe",
          "email": "jane.doe@example.com",
          "mobileNumber": "9876543210"
        }
        """;

    mockMvc
        .perform(
            post("/api/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.statusCode").value(AccountConstants.STATUS_201))
        .andExpect(jsonPath("$.message").value(AccountConstants.MESSAGE_201));

    verify(accountService).createAccount(any(CustomerDto.class));
  }

  @Test
  void createAccount_whenCustomerAlreadyExists_returns400BadRequest() throws Exception {
    doThrow(
            new CustomerAlreadyExistsException(
                "Customer with mobile number 9876543210 already exists"))
        .when(accountService)
        .createAccount(any(CustomerDto.class));

    String requestJson =
        """
        {
          "name": "Jane Doe",
          "email": "jane.doe@example.com",
          "mobileNumber": "9876543210"
        }
        """;

    mockMvc
        .perform(
            post("/api/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("400 BAD_REQUEST"))
        .andExpect(
            jsonPath("$.message").value("Customer with mobile number 9876543210 already exists"));
  }

  @Test
  void fetchAccountDetails_whenSuccess_returns200OkWithAccountDetails() throws Exception {
    CustomerDto customerDto = new CustomerDto();
    customerDto.setName("Jane Doe");
    customerDto.setEmail("jane.doe@example.com");
    customerDto.setMobileNumber("9876543210");
    AccountDto accountDto = new AccountDto();
    accountDto.setAccountNumber(1000000001L);
    accountDto.setAccountType("Savings");
    accountDto.setBranchAddress("123 Main Street");
    customerDto.setAccount(accountDto);

    when(accountService.fetchAccountDetails("9876543210")).thenReturn(customerDto);

    mockMvc
        .perform(get("/api/accounts/fetch").param("mobileNumber", "9876543210"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Jane Doe"))
        .andExpect(jsonPath("$.email").value("jane.doe@example.com"))
        .andExpect(jsonPath("$.mobileNumber").value("9876543210"))
        .andExpect(jsonPath("$.account.accountNumber").value(1000000001L))
        .andExpect(jsonPath("$.account.accountType").value("Savings"))
        .andExpect(jsonPath("$.account.branchAddress").value("123 Main Street"));

    verify(accountService).fetchAccountDetails("9876543210");
  }

  @Test
  void fetchAccountDetails_whenCustomerDoesNotExist_returns404NotFound() throws Exception {
    when(accountService.fetchAccountDetails("9876543210"))
        .thenThrow(new ResourceNotFoundException("Customer", "mobileNumber", "9876543210"));

    mockMvc
        .perform(get("/api/accounts/fetch").param("mobileNumber", "9876543210"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("404 NOT_FOUND"))
        .andExpect(
            jsonPath("$.message")
                .value("Customer not found with the given input data mobileNumber:9876543210"));
  }

  @Test
  void fetchAllAccountsDetails_whenSuccess_returns200OkWithAllAccounts() throws Exception {
    CustomerDto firstCustomer = new CustomerDto();
    firstCustomer.setName("Jane Doe");
    firstCustomer.setEmail("jane.doe@example.com");
    firstCustomer.setMobileNumber("9876543210");
    CustomerDto secondCustomer = new CustomerDto();
    secondCustomer.setName("John Doe");
    secondCustomer.setEmail("john.doe@example.com");
    secondCustomer.setMobileNumber("1234567890");

    when(accountService.fetchAllAccountsDetails())
        .thenReturn(List.of(firstCustomer, secondCustomer));

    mockMvc
        .perform(get("/api/accounts/fetchAll"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Jane Doe"))
        .andExpect(jsonPath("$[0].mobileNumber").value("9876543210"))
        .andExpect(jsonPath("$[1].name").value("John Doe"))
        .andExpect(jsonPath("$[1].mobileNumber").value("1234567890"));

    verify(accountService).fetchAllAccountsDetails();
  }

  @Test
  void updateAccount_whenSuccess_returns200Ok() throws Exception {
    when(accountService.updateCustomerAccountDetails(any(CustomerDto.class))).thenReturn(true);

    String requestJson =
        """
        {
          "name": "Jane Doe",
          "email": "jane.doe@example.com",
          "mobileNumber": "9876543210",
          "account": {
            "accountNumber": 1000000001,
            "accountType": "Savings",
            "branchAddress": "123 Main Street"
          }
        }
        """;

    mockMvc
        .perform(
            put("/api/accounts/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.statusCode").value(AccountConstants.STATUS_200))
        .andExpect(jsonPath("$.message").value(AccountConstants.MESSAGE_200));
  }

  @Test
  void updateAccount_whenFailure_returns417ExpectationFailed() throws Exception {
    when(accountService.updateCustomerAccountDetails(any(CustomerDto.class))).thenReturn(false);

    String requestJson =
        """
        {
          "name": "Jane Doe",
          "email": "jane.doe@example.com",
          "mobileNumber": "9876543210"
        }
        """;

    mockMvc
        .perform(
            put("/api/accounts/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isExpectationFailed())
        .andExpect(jsonPath("$.statusCode").value(AccountConstants.STATUS_417))
        .andExpect(jsonPath("$.message").value(AccountConstants.MESSAGE_417_UPDATE));
  }

  @Test
  void updateAccount_whenResourceNotFound_returns404NotFound() throws Exception {
    when(accountService.updateCustomerAccountDetails(any(CustomerDto.class)))
        .thenThrow(new ResourceNotFoundException("Account", "accountNumber", "9999999999"));

    String requestJson =
        """
        {
          "name": "Jane Doe",
          "email": "jane.doe@example.com",
          "mobileNumber": "9876543210",
          "account": {
            "accountNumber": 9999999999,
            "accountType": "Savings",
            "branchAddress": "123 Main Street"
          }
        }
        """;

    mockMvc
        .perform(
            put("/api/accounts/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("404 NOT_FOUND"))
        .andExpect(
            jsonPath("$.message")
                .value("Account not found with the given input data accountNumber:9999999999"));
  }

  @Test
  void updateAccount_whenCustomerNotFound_returns404NotFound() throws Exception {
    when(accountService.updateCustomerAccountDetails(any(CustomerDto.class)))
        .thenThrow(new ResourceNotFoundException("Customer", "customerId", "10"));

    String requestJson =
        """
        {
          "name": "Jane Doe",
          "email": "jane.doe@example.com",
          "mobileNumber": "9876543210",
          "account": {
            "accountNumber": 1000000001,
            "accountType": "Savings",
            "branchAddress": "123 Main Street"
          }
        }
        """;

    mockMvc
        .perform(
            put("/api/accounts/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("404 NOT_FOUND"))
        .andExpect(
            jsonPath("$.message")
                .value("Customer not found with the given input data customerId:10"));
  }

  @Test
  void deleteAccount_whenSuccess_returns200Ok() throws Exception {
    when(accountService.deleteAccount("9876543210")).thenReturn(true);

    mockMvc
        .perform(delete("/api/accounts/delete").param("mobileNumber", "9876543210"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.statusCode").value(AccountConstants.STATUS_200))
        .andExpect(jsonPath("$.message").value(AccountConstants.MESSAGE_200));

    verify(accountService).deleteAccount("9876543210");
  }

  @Test
  void deleteAccount_whenDeletionFails_returns417ExpectationFailed() throws Exception {
    when(accountService.deleteAccount("9876543210")).thenReturn(false);

    mockMvc
        .perform(delete("/api/accounts/delete").param("mobileNumber", "9876543210"))
        .andExpect(status().isExpectationFailed())
        .andExpect(jsonPath("$.statusCode").value(AccountConstants.STATUS_417))
        .andExpect(jsonPath("$.message").value(AccountConstants.MESSAGE_417_DELETE));
  }

  @Test
  void deleteAccount_whenCustomerDoesNotExist_returns404NotFound() throws Exception {
    when(accountService.deleteAccount("9876543210"))
        .thenThrow(new ResourceNotFoundException("Customer", "mobileNumber", "9876543210"));

    mockMvc
        .perform(delete("/api/accounts/delete").param("mobileNumber", "9876543210"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("404 NOT_FOUND"))
        .andExpect(
            jsonPath("$.message")
                .value("Customer not found with the given input data mobileNumber:9876543210"));
  }
}
