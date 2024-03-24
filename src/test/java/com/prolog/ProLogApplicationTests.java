package com.prolog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import org.junit.jupiter.api.Test;

class ProLogApplicationTests {
  @Test
  public void test() {
    String s = "b27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1ab27d5a8d-8b13-4eac-b39b-32711e650d53_1a";
    StringBuilder sb = new StringBuilder(s);
    StringBuilder sn = maskValue(sb, 0, s.length());
    System.out.println(    sn.toString());
  }
  private StringBuilder maskValue(StringBuilder sb, int start, int end) {
    // If the length of the value exceeds 40 characters, replace it with "***"
    if (end - start > 40) {
      sb.replace(start, end, "***");
    } else {
      // Mask characters from start to end (excluding the quotes)
      for (int i = start; i < end; i++) {
        sb.setCharAt(i, '*');
      }

    }
    return sb;
  }
}
