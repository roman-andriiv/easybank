package com.andriiv.easybank.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {
  private String statusCode;
  private String message;
}
