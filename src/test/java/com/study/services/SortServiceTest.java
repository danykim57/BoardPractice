package com.study.services;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SortServiceTest {
  SortService sortService = new SortService();

  @Test
  void get_the_highest_num() {
    List<Integer> integers = new ArrayList<>();
    integers.add(0);
    integers.add(-5);
    integers.add(15);
    Optional<Integer> res = sortService.getMaxNums(integers);
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
  void printFruits() {
  }
}