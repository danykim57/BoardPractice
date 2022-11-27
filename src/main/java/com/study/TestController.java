package com.study;

import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
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
  @ExceptionHandler({SQLException.class, DataAccessException.class})
  public String databaseError() {
    // Nothing to do.  Returns the logical view name of an error page, passed
    // to the view-resolver(s) in usual way.
    // Note that the exception is NOT available to this view (it is not added
    // to the model) but see "Extending ExceptionHandlerExceptionResolver"
    // below.
    return "databaseError";
  }


}
