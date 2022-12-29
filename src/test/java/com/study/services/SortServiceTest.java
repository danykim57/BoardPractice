package com.study.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SortServiceTest {

  SortService sortService = new SortService();

  @Test
  @EnabledOnJre({JRE.JAVA_11})
  void get_the_highest_num() {
    List<Integer> integers = new ArrayList<>();
    integers.add(0);
    integers.add(-5);
    integers.add(15);
    Optional<Integer> res = sortService.getMaxNums(integers);
    System.out.println(System.getenv("TEST_ENV"));
    assumeTrue("LOCAL".equalsIgnoreCase(System.getenv("TEST_ENV")));
    assertEquals(res.get(), 15);
  }

  @Test
  void get_the_lowest_num() {

  }

  @Test
  void getAverageNumDouble() {

  }

  @Test
  void getAverageNumInt() {
  }

  @Test
  @ParameterizedTest(name = "{index} {displayName} message={0} ")
  @ValueSource(strings = {"메세지", "테스트", "입니다"})
  void parameterizedTest(String messages) {
    System.out.println(messages);
  }

  @Test
  @ParameterizedTest(name = "{index} {displayName} message={0} ")
  @ValueSource(ints = {1, 2, 3})
  void parameterizedIntegersTest(Integer integers) {
    System.out.println(integers);
  }
}