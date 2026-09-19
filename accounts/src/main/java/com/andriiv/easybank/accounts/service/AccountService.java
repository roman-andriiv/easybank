package com.andriiv.easybank.accounts.service;

import com.andriiv.easybank.accounts.dto.CustomerDto;
import java.util.List;

/** Service interface for account-related operations. */
public interface AccountService {

  /**
   * Creates a new account based on the provided customer details.
   *
   * @param customerDto - {@link CustomerDto} Object
   */
  void createAccount(CustomerDto customerDto);

  /**
   * Fetches account details for the customer identified by the given mobile number.
   *
   * @param mobileNumber the customer's registered mobile number
   * @return the customer's account details
   */
  CustomerDto fetchAccountDetails(String mobileNumber);

  /**
   * Fetches account details for all customers.
   *
   * @return a list of customer account details
   */
  List<CustomerDto> fetchAllAccountsDetails();
}
