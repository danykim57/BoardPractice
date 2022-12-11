package com.study.interceptor;

import javax.annotation.Nullable;

public interface ChannelInterceptor {
  @Nullable
  default Message<?> preSend(Message<?> message, MessageChannel channel) {
    return message;
  }

  default void postSend(Message<?> message, MessageChannel channel, boolean sent) {
  }

  default void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
  }

  default boolean preReceive(MessageChannel channel) {
    return true;
  }

  @Nullable
  default Message<?> postReceive(Message<?> message, MessageChannel channel) {
    return message;
  }

  default void afterReceiveCompletion(@Nullable Message<?> message, MessageChannel channel, @Nullable Exception ex) {
  }
}
