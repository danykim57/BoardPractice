package com.study;

import com.study.model.PasswordResetToken;

import java.util.Calendar;

public class SecurityService {
  public String validatePasswordResetToken(String token) {
    final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

    return !isTokenFound(passToken) ? "invalidToken"
        : isTokenExpired(passToken) ? "expired"
        : null;
  }

  private boolean isTokenFound(PasswordResetToken passToken) {
    return passToken != null;
  }

  private boolean isTokenExpired(PasswordResetToken passToken) {
    final Calendar cal = Calendar.getInstance();
    return passToken.getExpiryDate().before(cal.getTime());
  }
}
