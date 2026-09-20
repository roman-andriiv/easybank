package com.andriiv.easybank.accounts.controller;

import com.andriiv.easybank.accounts.constants.AccountConstants;
import com.andriiv.easybank.accounts.dto.CustomerDto;
import com.andriiv.easybank.accounts.dto.Response;
import com.andriiv.easybank.accounts.service.AccountService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class AccountsController {

  private AccountService accountService;

  @PostMapping("/create")
  public ResponseEntity<Response> createAccount(@RequestBody CustomerDto customerDto) {
    accountService.createAccount(customerDto);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new Response(AccountConstants.STATUS_201, AccountConstants.MESSAGE_201));
  }

  @GetMapping("/fetch")
  public ResponseEntity<CustomerDto> fetchAccountDetails(@RequestParam String mobileNumber) {
    CustomerDto body = accountService.fetchAccountDetails(mobileNumber);
    return ResponseEntity.ok(body);
  }

  @GetMapping("/fetchAll")
  public ResponseEntity<List<CustomerDto>> fetchAllAccountsDetails() {
    return ResponseEntity.ok(accountService.fetchAllAccountsDetails());
  }

  @DeleteMapping("/delete")
  public ResponseEntity<Response> deleteAccountDetails(@RequestParam String mobileNumber) {
    boolean isDeleted = accountService.deleteAccount(mobileNumber);
    if (isDeleted) {
      return ResponseEntity.status(HttpStatus.OK)
          .body(new Response(AccountConstants.STATUS_200, AccountConstants.MESSAGE_200));
    } else {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
          .body(new Response(AccountConstants.STATUS_417, AccountConstants.MESSAGE_417_DELETE));
    }
  }

  @PutMapping("/update")
  public ResponseEntity<Response> updateAccountDetails(@RequestBody CustomerDto customerDto) {
    boolean isUpdated = accountService.updateCustomerAccountDetails(customerDto);
    if (isUpdated) {
      return ResponseEntity.status(HttpStatus.OK)
          .body(new Response(AccountConstants.STATUS_200, AccountConstants.MESSAGE_200));
    } else {
      return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
          .body(new Response(AccountConstants.STATUS_417, AccountConstants.MESSAGE_417_UPDATE));
    }
  }
}
