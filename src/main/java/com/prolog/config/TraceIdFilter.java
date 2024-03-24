package com.prolog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import org.springframework.web.filter.OncePerRequestFilter;

public class TraceIdFilter extends OncePerRequestFilter {

  private static final String TRACE_ID_ATTRIBUTE = "traceId";
  private static final Random random = new Random();  // Compliant
  private static String traceId;
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    request.setAttribute(TRACE_ID_ATTRIBUTE, generateTraceId());
    filterChain.doFilter(request, response);
  }

  public static String getTraceIdAttribute() {
    return traceId;
  }

  public static String generateTraceId() {
    long timestamp = System.currentTimeMillis();
    int randomDigits = random.nextInt(0,1000);
    long threadId = Thread.currentThread().getId();
    traceId = String.valueOf(timestamp)
        + randomDigits
        + threadId;
    return traceId;
  }

}
