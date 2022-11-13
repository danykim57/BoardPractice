package com.study.common.dto;

import lombok.Getter;

@Getter
public class CommonDto {
  private String success;
  private MessageDto message;

  public CommonDto(String success, MessageDto message) {
    this.success = success;
    this.message = message;
  }
}
