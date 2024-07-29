package com.prolog.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;
import lombok.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class TraceIdFilter extends OncePerRequestFilter {

  private static final String TRACE_ID_ATTRIBUTE = "traceId";
  private static final Random random = new Random();
  private static String traceId;

  public static synchronized String getTraceIdAttribute() {
    return traceId;
  }

  public static synchronized String generateTraceId() {
    long timestamp = System.currentTimeMillis();
    int randomDigits = random.nextInt(0, 1000);
    long threadId = Thread.currentThread().getId();
    traceId = String.valueOf(timestamp) + randomDigits + threadId;
    return traceId;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    request.setAttribute(TRACE_ID_ATTRIBUTE, generateTraceId());
    filterChain.doFilter(request, response);
  }

}
