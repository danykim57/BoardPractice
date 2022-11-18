package com.study.exception;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException() {
    super("없는 계정입니다.");
  }
}
