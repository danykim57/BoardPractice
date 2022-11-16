package com.study;

import com.study.common.dto.GenericResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.UUID;

public class PasswordController {
  @Autowired
  SecurityService securityService;

  @Autowired
  UserService userService;

  @PostMapping("/user/resetPassword")
  public GenericResponse resetPassword(HttpServletRequest request,
                                       @RequestParam("email") String userEmail) {
    User user = userService.findUserByEmail(userEmail);
    if (user == null) {
      throw new UserNotFoundException();
    }
    String token = UUID.randomUUID().toString();
    userService.createPasswordResetTokenForUser(user, token);
    mailSender.send(constructResetTokenEmail(getAppUrl(request),
        request.getLocale(), token, user));
    return new GenericResponse(
        messages.getMessage("message.resetPasswordEmail", null,
            request.getLocale()));
  }

  @GetMapping("/user/changePassword")
  public String showChangePasswordPage(Locale locale, Model model,
                                       @RequestParam("token") String token) {
    String result = securityService.validatePasswordResetToken(token);
    if(result != null) {
      String message = messages.getMessage("auth.message." + result, null, locale);
      return "redirect:/login.html?lang="
          + locale.getLanguage() + "&message=" + message;
    } else {
      model.addAttribute("token", token);
      return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
    }
  }

  @PostMapping("/user/savePassword")
  public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {

    String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());

    if(result != null) {
      return new GenericResponse(messages.getMessage(
          "auth.message." + result, null, locale));
    }

    Optional user = userService.getUserByPasswordResetToken(passwordDto.getToken());
    if(user.isPresent()) {
      userService.changeUserPassword(user.get(), passwordDto.getNewPassword());
      return new GenericResponse(messages.getMessage(
          "message.resetPasswordSuc", null, locale));
    } else {
      return new GenericResponse(messages.getMessage(
          "auth.message.invalid", null, locale));
    }
  }
}
