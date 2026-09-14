package com.andriiv.easybank.accounts.mapper;

import com.andriiv.easybank.accounts.dto.CustomerDto;
import com.andriiv.easybank.accounts.entity.Customer;

public class CustomerMapper {
  private CustomerMapper() {
    /* This utility class should not be instantiated */
  }

  public static CustomerDto mapToCustomerDto(Customer customer, CustomerDto customerDto) {
    customerDto.setName(customer.getName());
    customerDto.setEmail(customer.getEmail());
    customerDto.setMobileNumber(customer.getMobileNumber());
    return customerDto;
  }

  public static Customer mapToCustomer(CustomerDto customerDto, Customer customer) {
    customer.setName(customerDto.getName());
    customer.setEmail(customerDto.getEmail());
    customer.setMobileNumber(customerDto.getMobileNumber());
    return customer;
  }
}
