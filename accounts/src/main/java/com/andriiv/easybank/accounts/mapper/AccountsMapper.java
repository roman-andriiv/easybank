package com.andriiv.easybank.accounts.mapper;

import com.andriiv.easybank.accounts.dto.AccountDto;
import com.andriiv.easybank.accounts.entity.Account;

public class AccountsMapper {
  private AccountsMapper() {
    /* This utility class should not be instantiated */
  }

  public static AccountDto mapToAccountsDto(Account account, AccountDto accountsDto) {
    accountsDto.setAccountNumber(account.getAccountNumber());
    accountsDto.setAccountType(account.getAccountType());
    accountsDto.setBranchAddress(account.getBranchAddress());
    return accountsDto;
  }

  public static Account mapToAccounts(AccountDto accountsDto, Account account) {
    account.setAccountNumber(accountsDto.getAccountNumber());
    account.setAccountType(accountsDto.getAccountType());
    account.setBranchAddress(accountsDto.getBranchAddress());
    return account;
  }
}
