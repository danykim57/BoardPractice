package com.study.services;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
  
  Stream<String> fruits = Stream.of("banana", "apple", "mango", "kiwi", "peach", "cherry", "lemon");
  HashSet<String> fruitHashSet = fruits.collect(HashSet::new, HashSet::add, HashSet::addAll);
  for (String s : fruitHashSet) {
      System.out.println(s);
  }
}
