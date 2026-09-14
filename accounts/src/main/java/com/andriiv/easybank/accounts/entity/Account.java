package com.andriiv.easybank.accounts.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {
  private Long customerId;
  @Id private Long accountNumber;
  private String accountType;
  private String branchAddress;
}
