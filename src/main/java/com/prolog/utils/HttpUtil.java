package com.prolog.utils;

import static com.prolog.enums.ExceptionMessage.UTILITY_CLASS_CANNOT_BE_INSTANTIATED;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

  private HttpUtil() {
    throw new UnsupportedOperationException(UTILITY_CLASS_CANNOT_BE_INSTANTIATED.message);
  }

  public static Map<String, String> extractRequestHeaders(HttpServletRequest httpServletRequest) {
    Map<String, String> headers = new HashMap<>();
    Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = httpServletRequest.getHeader(headerName);
      headers.put(headerName, headerValue);
    }
    return headers;
  }


}
