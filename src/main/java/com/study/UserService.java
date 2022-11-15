package com.study;

import com.study.model.PasswordResetToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  public void createPasswordResetTokenForUser(User user, String token) {
    PasswordResetToken myToken = new PasswordResetToken(token, user);
    passwordTokenRepository.save(myToken);
  }

  public void changeUserPassword(User user, String password) {
    user.setPassword(passwordEncoder.encode(password));
    repository.save(user);
  }
}
