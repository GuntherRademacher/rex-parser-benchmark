package de.bottlecaps.rex.benchmark.json.parsers;

import java.util.List;

public interface Parser extends Comparable<Parser> {

  Object parse(String input) throws Exception;

  Double getMaxSpeed();

  void addPerformanceData(long parsedSize, long runtime, long maxMemory);

  long getParsedSize();

  long getRuntime();

  void clear();

  @Override
  int compareTo(Parser other);

  List<Double> getSpeedSeries();

  List<Double> getMemorySeries();

  long getMaxMemory();

  default String name() {
    return getClass().getSimpleName();
  }

}