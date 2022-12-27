package com.study.services;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SortService {

  public Optional<Integer> getMaxNums(List<Integer> integers) {
    final Comparator<Integer> comp = (p1, p2) -> Integer.compare( p1, p2);
    return integers.stream().max(comp);
  }

  public Map<Integer, List<Integer>> getMinNums(List<Integer> integers) {
    return integers.stream().collect(Collectors.groupingBy(i -> integers.get(i)) );
  }

  public Double getAverageNumDouble(List<Integer> integers) {
    return integers.stream().collect(Collectors.averagingDouble(i -> integers.get(i)));
  }

  public Double getAverageNumInt(List<Integer> integers) {
    return integers.stream().collect(Collectors.averagingInt(i -> integers.get(i)));
  }

  public void printFruits() {
    Stream<String> fruits = Stream.of("banana", "apple", "mango", "kiwi", "peach", "cherry", "lemon");
    HashSet<String> fruitHashSet = fruits.collect(HashSet::new, HashSet::add, HashSet::addAll);
  }
}
