package com.prolog.controller;

import com.prolog.annotations.RestLog;
import com.prolog.annotations.ServiceLog;
import com.prolog.dto.Body;
import com.prolog.enums.LogLevel;
import com.prolog.service.ServiceTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RestLog
public class TestController {

  private final ServiceTest serviceTest;

  public TestController(ServiceTest serviceTest) {
    this.serviceTest = serviceTest;
  }


  @GetMapping("/hello/{id}")
  public String helloPrint(@PathVariable("id") String hello) {

    return serviceTest.printHelloFromService();
  }

  @PostMapping("/post")
  public Body hello(@RequestBody Body body) {
    return body;
  }

}
