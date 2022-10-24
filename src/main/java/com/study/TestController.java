package com.study;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TestController {

  @GetMapping("/members")
  public List<Map<String, Object>> findAllMember() {
    List<Map<String, Object>> members = new ArrayList<>();
    for (int i = 1; i <= 20; i++) {
      Map<String, Object> member = new HashMap<>();
      member.put("id", i);
      member.put("name", "test" + i);
      members.add(member);
    }
    return members;
  }

}
