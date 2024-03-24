package com.prolog.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpUtilTest {

  @Mock
  private HttpServletRequest httpServletRequest;


  @Test
  void itShouldExtractRequestHeaders() {
    /* GIVEN */
    Enumeration<String> headerNames = Collections.enumeration (
        Map.of("Header1", "Value1", "Header2", "Value2").keySet()
    );

    /* WHEN */
    when(httpServletRequest.getHeaderNames()).thenReturn(headerNames);
    when(httpServletRequest.getHeader("Header1")).thenReturn("Value1");
    when(httpServletRequest.getHeader("Header2")).thenReturn("Value2");

    Map<String, String> extractedHeaders = HttpUtil.extractRequestHeaders(httpServletRequest);

    /* THEN */
    assertEquals(2, extractedHeaders.size());
    assertEquals("Value1", extractedHeaders.get("Header1"));
    assertEquals("Value2", extractedHeaders.get("Header2"));
  }
}