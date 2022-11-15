package com.study.common.dto;

public class PasswordDto {

  private String oldPassword;

  private  String token;

  @ValidPassword
  private String newPassword;
}
