package de.bottlecaps.rex.benchmark.json;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.bottlecaps.rex.benchmark.json.parsers.Parser;
import de.bottlecaps.rex.benchmark.json.parsers.java.ANTLR4;
import de.bottlecaps.rex.benchmark.json.parsers.java.Grammatica;
import de.bottlecaps.rex.benchmark.json.parsers.java.HandCrafted;
import de.bottlecaps.rex.benchmark.json.parsers.java.Jackson;
import de.bottlecaps.rex.benchmark.json.parsers.java.JavaCC;
import de.bottlecaps.rex.benchmark.json.parsers.java.REx_LALR;
import de.bottlecaps.rex.benchmark.json.parsers.java.REx_LL;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.BaseX;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.BaseXIxml;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.BaseXRExLALR;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.BaseXRExLALRExternal;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.BaseXRExLL;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.BaseXRExLLExternal;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.Saxon;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.SaxonIxmlEarley;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.SaxonRExLALR;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.SaxonRExLALRExternal;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.SaxonRExLL;
import de.bottlecaps.rex.benchmark.json.parsers.xquery.SaxonRExLLExternal;

public class CommandLine {

  private static final Parser[] parsersInJava = new Parser[] {
      new ANTLR4(),
      new Grammatica(),
      new HandCrafted(),
      new Jackson(),
      new JavaCC(),
      new REx_LL(),
      new REx_LALR(),
  };

  private static final Parser[] parsersInXQuery = new Parser[] {
      new BaseX(),
      new BaseXRExLLExternal(),
      new BaseXRExLALRExternal(),
      new BaseXRExLL(),
      new BaseXRExLALR(),
      new BaseXIxml(),
      new Saxon(),
      new SaxonRExLLExternal(),
      new SaxonRExLALRExternal(),
      new SaxonRExLL(),
      new SaxonRExLALR(),
      new SaxonIxmlEarley(),
//      disabled, waiting for https://github.com/nineml/nineml/issues/42
//      new SaxonIxmlGLL(),
  };

  private List<Parser> testParsers;
  private List<String> exclude = new ArrayList<>();
  private List<String> include = new ArrayList<>();
  private enum Platform {JAVA, XQUERY};
  Platform platform = Platform.JAVA;
  private File inputFile = null;
  private boolean validation = true;
  private boolean createResult = false;
  private int warmUp = 10;
  private int parseTime = 10;
  private int factor = 2;
  private boolean nest = false;
  private Long heapDumpAt = null;

  public CommandLine(String[] args) {
    for (int a = 0; a < args.length; ++a) {
      String arg = args[a];
      switch (arg) {
      case "-?":
      case "--help":
        usage(0);
        return;
      case "--novalidation":
        validation = false;
        break;
      case "--platform":
        try {
          platform = Platform.valueOf(args[++a].toUpperCase());
        }
        catch (IllegalArgumentException e) {
          error("invalid --platform specification");
        }
        break;
      case "--exclude":
        exclude.add(args[++a].toLowerCase());
        break;
      case "--include":
        include.add(args[++a].toLowerCase());
        break;
      case "--warmup":
        try {
          warmUp = Integer.parseInt(args[++a]);
        }
        catch (Exception e) {
          error("invalid --warmup option");
        }
        break;
      case "--time":
        try {
          parseTime = Integer.parseInt(args[++a]);
        }
        catch (Exception e) {
          error("invalid --time option");
        }
        break;
      case "--create-result":
        createResult = true;
        break;
      case "--factor":
        try {
          factor = Integer.parseInt(args[++a]);
          if (factor > 0)
            break;
        }
        catch (Exception e) {
        }
        error("invalid --factor option");
        break;
      case "--nest":
        nest = true;
        break;
      case "--heapdump":
        try {
          heapDumpAt = Size.valueOf(args[++a]);
          if (heapDumpAt > 100_000_000)
            break;
        }
        catch (Exception e) {
        }
        error("invalid --heapdump option");
        break;
      default:
        if (arg.startsWith("-"))
          error("invalid option: " + arg);
        if (inputFile != null)
          error("too many input folder specifications: " + inputFile.getName() + ", " + arg);
        inputFile = new File(arg);
      }
    }

    if (inputFile == null)
      inputFile = new File(".");
    switch (platform) {
    default:
      testParsers = Arrays.asList(parsersInJava);
      break;
    case XQUERY:
      testParsers = Arrays.asList(parsersInXQuery);
      break;
    }
    testParsers = testParsers.stream()
      .filter(parser -> include.isEmpty() || include.contains(parser.name().toLowerCase()))
      .filter(parser -> ! exclude.contains(parser.name().toLowerCase()))
      .collect(Collectors.toList());
  }

  private static void error(String msg) {
    System.out.println(msg);
    System.out.println();
    usage(1);
  }

  private static void usage(int status) {
    System.out.println("Usage: java Benchmark <OPTION>... [<FILE>|<DIRECTORY>]");
    System.out.println();
    System.out.println("  read JSON file, or all *.json files in given directory (default: current dir). Restrict");
    System.out.println("  to those that are parseable by all parsers. Parse repeatedly. Log execution time.");
    System.out.println();
    System.out.println("  Options:");
    System.out.println();
    System.out.println("  -?, --help               show this message");
    System.out.println("  --platform [java|xquery] use Java or XQuery parser set");
    System.out.println("                             (default: java)");
    System.out.println("  --exclude <PARSER>       exclude <PARSER> from test");
    System.out.println("  --include <PARSER>       include <PARSER> in test");
    System.out.println("  --novalidation           skip comparison of parsing results");
    System.out.println("  --create-result          create result JsonObject (Java only,");
    System.out.println("                             XQuery always is executed with results)");
    System.out.println("  --warmup <TIME>          warm up parsers for <TIME> seconds");
    System.out.println("                             (default: 10)");
    System.out.println("  --time <TIME>            run each parser for <TIME> seconds");
    System.out.println("                             (default: 10)");
    System.out.println("  --factor <FACTOR>        increase input size by <FACTOR>");
    System.out.println("                             after each test cycle (default 2)");
    System.out.println("  --nest                   nest JSON arrays, when increasing input size. By");
    System.out.println("                             default, a single top level array will be used.");
    System.out.println("  --heapdump <SIZE>        dump heap when reaching <SIZE> (may contain fraction");
    System.out.println("                             and unit MB or GB) to file java_<PID>.hprof.");
    System.exit(status);
  }

  public boolean validation() {
    return validation;
  }

  public File inputFile() {
    return inputFile;
  }

  public List<Parser> testParsers() {
    return testParsers;
  }

  public boolean createResult() {
    return createResult;
  }

  public int warmUp() {
    return warmUp;
  }

  public int parseTime() {
    return parseTime;
  }

  public int factor() {
    return factor;
  }

  public boolean nest() {
    return nest;
  }

  public Long heapDumpAt() {
    return heapDumpAt;
  }
}
