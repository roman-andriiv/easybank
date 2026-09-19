package com.andriiv.easybank.accounts.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResponse {
  private String apiPath;
  private HttpStatus errorCode;
  private String message;
  private LocalDateTime timestamp;
}
