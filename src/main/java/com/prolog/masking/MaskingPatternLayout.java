package com.prolog.masking;


import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.prolog.annotations.RestLog;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;


public class MaskingPatternLayout extends PatternLayout {

  private static boolean maskEnabled;
  private final List<String> maskPatterns = new ArrayList<>();
  private Pattern multilinePattern;

  public static void enableMaskingIfAnnotated(Object object) {
    Class<?> clazz = object.getClass();
    if (clazz.isAnnotationPresent(RestLog.class)) {
      RestLog annotation = clazz.getAnnotation(RestLog.class);
      maskEnabled = annotation.hideSensitiveData();
    }
  }

  public void addMaskPattern(String maskPattern) {
    maskPatterns.add(maskPattern);
    multilinePattern = Pattern.compile(String.join("|", maskPatterns), Pattern.MULTILINE);
  }

  @Override
  public String doLayout(ILoggingEvent event) {
    return maskEnabled ? maskMessage(super.doLayout(event)) : super.doLayout(event);
  }

  private String maskMessage(String message) {
    if (multilinePattern == null) {
      return message;
    }
    StringBuilder sb = new StringBuilder(message);
    Matcher matcher = multilinePattern.matcher(sb);
    while (matcher.find()) {
      IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
        if (matcher.group(group) != null) {
          IntStream.range(matcher.start(group), matcher.end(group)).forEach(i -> sb.setCharAt(i, '*'));
        }
      });
    }
    return sb.toString();
  }
}

