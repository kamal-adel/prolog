package com.prolog.service;

import com.prolog.annotations.ServiceLog;
import org.springframework.stereotype.Service;

@Service
@ServiceLog
public class ServiceTest {

  public String printHelloFromService() {
    return "HELLO FROM SERVICE";
  }

}
