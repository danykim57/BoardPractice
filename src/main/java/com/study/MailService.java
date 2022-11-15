package com.study;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MailService {
  private SimpleMailMessage constructResetTokenEmail(
      String contextPath, Locale locale, String token, User user) {
    String url = contextPath + "/user/changePassword?token=" + token;
    String message = messages.getMessage("message.resetPassword",
        null, locale);
    return constructEmail("Reset Password", message + " \r\n" + url, user);
  }

  private SimpleMailMessage constructEmail(String subject, String body,
                                           User user) {
    SimpleMailMessage email = new SimpleMailMessage();
    email.setSubject(subject);
    email.setText(body);
    email.setTo(user.getEmail());
    email.setFrom(env.getProperty("support.email"));
    return email;
  }
}
