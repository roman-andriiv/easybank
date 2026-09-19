package com.andriiv.easybank.accounts.service;

import com.andriiv.easybank.accounts.dto.CustomerDto;

/** Service interface for account-related operations. */
public interface AccountService {

  /**
   * Creates a new account based on the provided customer details.
   *
   * @param customerDto - {@link CustomerDto} Object
   */
  void createAccount(CustomerDto customerDto);
}
