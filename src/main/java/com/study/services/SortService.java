package com.study.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SortService {

  public Optional<Integer> getMaxNumbs(List<Integer> integers) {
    final Comparator<Integer> comp = (p1, p2) -> Integer.compare( p1, p2);
    return integers.stream().max(comp);
  }
}
