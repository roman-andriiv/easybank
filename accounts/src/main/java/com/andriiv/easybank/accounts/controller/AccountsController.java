package com.andriiv.easybank.accounts.controller;

import com.andriiv.easybank.accounts.constants.AccountConstants;
import com.andriiv.easybank.accounts.dto.CustomerDto;
import com.andriiv.easybank.accounts.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountsController {

  @PostMapping("/create")
  public ResponseEntity<Response> createAccount(@RequestBody CustomerDto customerDto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new Response(AccountConstants.STATUS_201, AccountConstants.MESSAGE_201));
  }
}
