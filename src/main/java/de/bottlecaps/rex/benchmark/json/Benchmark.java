package de.bottlecaps.rex.benchmark.json;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import de.bottlecaps.rex.benchmark.json.Chart.Builder;
import de.bottlecaps.rex.benchmark.json.Chart.LinearSizeFormat;
import de.bottlecaps.rex.benchmark.json.Chart.LogarithmicSizeFormat;
import de.bottlecaps.rex.benchmark.json.parsers.DummyJsonNodeFactory;
import de.bottlecaps.rex.benchmark.json.parsers.Parser;
import de.bottlecaps.rex.benchmark.json.parsers.ResultFactory;

public class Benchmark {

  public static void main(String[] args) throws Exception {
    CommandLine commandLine = new CommandLine(args);

    if (commandLine.validation())
      ResultFactory.setJsonNodeFactory(JsonNodeFactory.instance);
    else
      ResultFactory.setJsonNodeFactory(DummyJsonNodeFactory.instance);

    if (commandLine.testParsers().isEmpty()) {
      System.out.println("no matching parsers - cannot run benchmark");
      System.exit(1);
    }

    File inputFile = commandLine.inputFile();
    Map<String, String> inputFiles = inputFile.isDirectory()
        ? getAllFiles(inputFile, ".json", new HashMap<>())
        : new HashMap<>(Collections.singletonMap(inputFile.getAbsolutePath(), fileContent(inputFile)));

    int[] parsedOK = new int[commandLine.testParsers().size()];
    Set<String> badFiles = new HashSet<>();

    for (Entry<String, String> file : inputFiles.entrySet()) {
      String name = file.getKey();
      System.out.println("parsing " + name);
      String content = file.getValue();
      Object referenceResult = null;
      int p = 0;
      for (Parser parser : commandLine.testParsers()) {
        System.out.println("  with " + parser.name());
        Object result = null;
        try {
          result = parser.parse(content);
          if (result == null) {
            String msg = parser.name() + "(" + name + "): missing result";
            System.out.println(msg);
            System.out.println();
          }
        }
        catch (Throwable e) {
          String msg = parser.name() + "(" + name + "): " + e.getMessage();
          System.out.println(msg);
          System.out.println();
        }
        if (result == null) {
          badFiles.add(name);
        }
        else {
          if (commandLine.validation()) {
            if (referenceResult == null)
              referenceResult = result;
            else if (! result.equals(referenceResult)) {
              if (result instanceof String && referenceResult instanceof String) {
                String s1 = (String) result;
                String sR = (String) referenceResult;
                for (int i = 0;; ++i)
                  if (s1.charAt(i) != sR.charAt(i)) {
                    System.err.println("     index: " + i + ", actual: " + (int) s1.charAt(i) + ", expected: " + (int) sR.charAt(i));
                    System.err.println("    actual: " + s1.substring(i, i + Math.min(80, s1.length())));
                    System.err.println("  expected: " + sR.substring(i, i + Math.min(80, sR.length())));
                    break;
                  }
              }
              throw new RuntimeException(parser.name() + " returns incorrect result on " + name);
            }
          }
          ++parsedOK[p];
        }
        ++p;
      }
    }

    System.out.println();
    System.out.println(inputFiles.size() + " files read");
    for (int p = 0; p < commandLine.testParsers().size(); ++p)
      System.out.println(parsedOK[p] + " parsed OK by " + commandLine.testParsers().get(p).name());

    for (String badFile : badFiles)
      inputFiles.remove(badFile);
    if (inputFiles.isEmpty()) {
      System.out.println("no parseable files - cannot run benchmark");
      System.exit(1);
    }

    long total = 0;
    for (String content : inputFiles.values())
      total += content.getBytes().length;
    System.out.println("using " + inputFiles.size() + " files (total size " + Chart.formatSize(total, 1) + ")");

    if (commandLine.createResult())
      ResultFactory.setJsonNodeFactory(JsonNodeFactory.instance);
    else
      ResultFactory.setJsonNodeFactory(DummyJsonNodeFactory.instance);

    if (commandLine.warmUp() > 0) {
      System.out.println();
      System.out.println("warming up parsers for at least " + commandLine.warmUp() + " sec each");
      for (Parser parser : commandLine.testParsers()) {
        System.out.println(" - " + parser.name());
        for (long warmupStart = System.currentTimeMillis();;) {
          for (String input : inputFiles.values())
            parser.parse(input);
          if (System.currentTimeMillis() - warmupStart > commandLine.warmUp() * 1000)
            break;
        }
      }
    }

    Map<String, String> files = new HashMap<>(inputFiles);
    int factor = 1;

    MemoryMonitor memoryMonitor = new MemoryMonitor();
    memoryMonitor.start();

    try {
      System.out.println();
      List<Double> sizeSeries = new ArrayList<>();
      for (int r = 0;; ++r) {
        if (commandLine.parseTime() > 0) {
          double avgSize = (double) total / files.size();
          sizeSeries.add(avgSize);

          System.out.println("parsing for at least " + commandLine.parseTime() + " sec per parser (average input size "
              + Chart.formatSize((int) avgSize, 1) + ")");

          long parseTimeInMsec = commandLine.parseTime() * 1000;
          for (Parser parser : commandLine.testParsers()) {
            System.out.println(" - " + parser.name());
            parser.clear();
            long parsedSize = 0;
            long start = System.currentTimeMillis();
  //          System.out.println("Used Memory before gc: " + Chart.formatSize(MemoryMonitor.memoryUsed(), 0));
            MemoryMonitor.triggerGC();
            long initialMemoryUsed = memoryMonitor.resetMaxMemoryUsed();
  //          System.out.println("Used Memory  after gc: " + Chart.formatSize(initialMemoryUsed, 0));
            for (;;) {
              for (String input : files.values())
                parser.parse(input);
              parsedSize += total;
              long runtime = System.currentTimeMillis() - start;
              if (runtime > parseTimeInMsec) {
                parser.addPerformanceData(parsedSize, runtime, memoryMonitor.maxMemoryUsed() - initialMemoryUsed);
                break;
              }
            }
          }

          System.out.println();
          boolean initial = r == 0;
          commandLine.testParsers().stream().sorted().forEach(p -> {
            String size = Chart.formatSize(p.getParsedSize(), 1);
            double seconds = p.getRuntime() / 1000.0;
            String speed = Chart.formatSize((long) (1e3 * p.getParsedSize() / p.getRuntime()), 1);
            String maxSpeed = Chart.formatSize((long) (p.getMaxSpeed().doubleValue() * 1e3), 1);
            String maxMemory = Chart.formatSize(p.getMaxMemory(), 1);
            if (initial)
              System.out.println(String.format(Locale.US, "%20s: parsed %s in %.1f sec (%s/sec), max memory: %s",
                  p.name(), size, seconds, speed, maxMemory));
            else
              System.out
                  .println(String.format(Locale.US, "%20s: parsed %s in %.1f sec (%s/sec, max: %s/sec), max memory: %s",
                      p.name(), size, seconds, speed, maxSpeed, maxMemory));
          });

          try (PrintStream throughputCsv = new PrintStream("throughput.csv", StandardCharsets.UTF_8);
               PrintStream memoryCsv = new PrintStream("memory.csv", StandardCharsets.UTF_8)) {
            String header = "log" + commandLine.factor() + "(inputSize),"
                    + commandLine.testParsers().stream().map(Parser::name).collect(Collectors.joining(","));
            throughputCsv.println(header);
            memoryCsv.println(header);
            for (int s = 0; s < sizeSeries.size(); ++s) {
              int sizeIndex = s;
              String throughputList = commandLine.testParsers().stream()
                  .map(p -> String.valueOf(p.getSpeedSeries().get(sizeIndex).floatValue()))
                  .collect(Collectors.joining(","));
              float logarithmicSize = (float) (Math.log(sizeSeries.get(s)) / Math.log(commandLine.factor()));
              throughputCsv.println(logarithmicSize + "," + throughputList);
              String memoryList = commandLine.testParsers().stream()
                  .map(p -> String.valueOf(p.getMemorySeries().get(sizeIndex).floatValue() / 1e6))
                  .collect(Collectors.joining(","));
              memoryCsv.println(logarithmicSize + "," + memoryList);
            }
          }

          int maxParseTime = 20 * commandLine.parseTime();
          if (commandLine.testParsers().stream().anyMatch(p -> p.getRuntime() / 1000 > maxParseTime)) {
            System.out.println();
            System.out.println("stopping, because maximum allowed parse time has been exceeded");
            break;
          }
        }

        System.out.println();

        if (commandLine.factor() > 1) {
            factor = commandLine.nest()
                 ? commandLine.factor()
                 : factor * commandLine.factor();
          total = 0;
          for (Map.Entry<String, String> e : files.entrySet()) {
            StringBuilder sb = new StringBuilder();
            String value = commandLine.nest()
                ? e.getValue()
                : inputFiles.get(e.getKey());
            sb.append('[');
            for (int i = 0; i < factor; ++i) {
              if (i > 0)
                sb.append(',');
              sb.append(value);
            }
            sb.append(']');
            e.setValue(sb.toString());
            total += sb.length();
          }
          System.out.println("increased input size by a factor of " + commandLine.factor() + " (total size now "
              + Chart.formatSize(total, 1) + ")");
        }
      }
    }
    catch (OutOfMemoryError e) {
      System.out.println("stopping, because an OutOfMemoryError was caught");
      memoryMonitor.stopMonitoring();
    }

    System.out.println("creating result chart...");
    csvToPng();
  }

  private static Map<String, String> getAllFiles(File dir, String extension, Map<String, String> contents)
      throws Exception {
    for (File file : dir.listFiles())
      if (file.isDirectory())
        getAllFiles(file, extension, contents);
      else if (file.getName().endsWith(extension))
        contents.put(file.getPath(), fileContent(file));
    return contents;
  }

  private static String fileContent(File file) throws IOException {
    return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
  }

  public static void csvToPng() throws IOException {
//    String csv = "log2(inputSize),ANTLR4,Grammatica,Hand-crafted,Jackson,JacksonO,JavaCC,REx\n"
//        + "12.962112,33113.227,14492.541,319122.9,296666.62,315033.4,67010.07,124262.78\n"
//        + "13.962384,43772.832,19087.824,344585.47,332386.5,359224.25,70540.13,119837.1\n"
//        + "14.96252,41702.74,18474.467,321796.44,271273.53,280746.56,67544.19,126881.375..."
    String[][] throughputCsv = readCsv("throughput.csv");
    String[][] memoryCsv = readCsv("memory.csv");
    int base = Integer.parseInt(throughputCsv[0][0].substring("log".length(), throughputCsv[0][0].indexOf('(')));
    Builder builder = new Builder()
        .chartTitle("Parser Throughput by Input Size")
        .xAxisFormat(new LogarithmicSizeFormat(base))
        .xAxisLabel("Input Size")
        .yAxisLabel1("Throughput (KB/sec)")
        .yAxisLabel2("Maximum Heap Size")
        ;
    for (int column = 1; column < memoryCsv[0].length; ++column) {
      String label = memoryCsv[0][column];
      builder.addSeries1(label, series(throughputCsv, column, 1e0));
      builder.addSeries2(label, series(memoryCsv, column, 1e6));
    }
    DecimalFormat yAxisFormat = new DecimalFormat();
    yAxisFormat.setGroupingUsed(false);
    builder.yAxisFormat1(yAxisFormat);
    builder.yAxisFormat2(new LinearSizeFormat());
    Chart chart1 = builder.build1();
    chart1.writeToFile("throughput.png");
    Chart chart2 = builder.build2();
    chart2.writeToFile("memory.png");
    chart1.displayChart("Throughput");
    chart2.displayChart("Memory");
  }

  private static double[][] series(String[][] lines, int column, double factor) {
    double[] x = Arrays.stream(lines)
        .skip(1)
        .filter(line -> line[column] != null)
        .mapToDouble(line -> Double.parseDouble(line[0]))
        .toArray();
    double[] y = Arrays.stream(lines)
        .skip(1)
        .filter(line -> line[column] != null)
        .mapToDouble(line -> Double.parseDouble(line[column]) * factor)
        .toArray();
    double[][] series = new double[][] { x, y };
    return series;
  }

  private static String[][] readCsv(String filename) throws IOException {
    String csv = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
    String[][] lines = Arrays.stream(csv.split("\n", -1))
        .filter(line -> line.trim().length() > 0)
        .map(line -> Arrays.stream(line.split(",", -1))
            .map(cell -> cell.trim().length() == 0 ? null : cell)
            .toArray(String[]::new))
        .toArray(String[][]::new);
    return lines;
  }

}
