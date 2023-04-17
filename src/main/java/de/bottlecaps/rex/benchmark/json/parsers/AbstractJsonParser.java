package de.bottlecaps.rex.benchmark.json.parsers;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJsonParser implements Parser {
  private long runtime = 0;
  private long parsedSize = 0;
  private Double maxSpeed = null;
  private long maxMemory = 0;
  private List<Double> speedSeries = new ArrayList<>();
  private List<Double> memorySeries = new ArrayList<>();

  @Override
  public long getParsedSize() {
    return parsedSize;
  }

  @Override
  public long getRuntime() {
    return runtime;
  }

  @Override
  public Double getMaxSpeed() {
    return maxSpeed;
  }

  @Override
  public long getMaxMemory() {
    return maxMemory;
  }

  @Override
  public List<Double> getSpeedSeries() {
    return speedSeries;
  }

  @Override
  public List<Double> getMemorySeries() {
    return memorySeries;
  }

  @Override
  public void addPerformanceData(long parsedSize, long runtime, long maxMemory) {
    this.parsedSize += parsedSize;
    this.runtime += runtime;
    this.maxMemory = maxMemory;
    double speed = (double) parsedSize / runtime; // MB/msec
    this.maxSpeed = this.maxSpeed == null ? speed : Math.max(maxSpeed, speed);
    this.speedSeries.add(speed);
    this.memorySeries.add((double) maxMemory);
  }

  @Override
  public void clear() {
    this.runtime = 0;
    this.parsedSize = 0;
  }

  @Override
  public int compareTo(Parser other) {
    return Integer.signum((int) (1024.0 * (getMaxSpeed() - other.getMaxSpeed())));
  }
}