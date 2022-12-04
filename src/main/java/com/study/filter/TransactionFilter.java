package com.study.filter;

import jdk.internal.org.jline.utils.Log;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(1)
public class TransactionFilter implements Filter {

  @Override
  public void doFilter(
      ServletRequest request,
      ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;
        Log.info("Starting a transaction for req : {}",
        req.getRequestURI());

        chain.doFilter(request, response);
        Log.info(
        "Committing a transaction for req : {}",
        req.getRequestURI());
  }

  // other methods
}