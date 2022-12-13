package com.study.socket;

import com.study.security.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

  private final Jwt jwt;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
    if(accessor.getCommand() == StompCommand.CONNECT) {
      Jwt.Claims claims = jwt.verify(accessor.getFirstNativeHeader("Authorization"));
      long userKey = claims.userKey();
      System.out.println("UserKey: " + userKey + "!!");
    }
    return message;
  }
}
