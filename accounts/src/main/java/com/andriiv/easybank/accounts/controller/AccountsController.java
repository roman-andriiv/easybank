package com.andriiv.easybank.accounts.controller;

import com.andriiv.easybank.accounts.constants.AccountConstants;
import com.andriiv.easybank.accounts.dto.CustomerDto;
import com.andriiv.easybank.accounts.dto.Response;
import com.andriiv.easybank.accounts.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
}
