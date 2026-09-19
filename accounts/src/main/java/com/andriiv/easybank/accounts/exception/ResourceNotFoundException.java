package com.andriiv.easybank.accounts.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String resource, String key, String value) {
    super(String.format("%s not found with the given input data %s:%s", resource, key, value));
  }
}
