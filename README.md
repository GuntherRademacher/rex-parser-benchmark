# REx Parser Benchmark

This is a comparison of the performance of parsers generated by [REx Parser Generator](https://bottlecaps.de/rex) to parsers from other parser generators. The task for each parser is to parse JSON input and create a result data structure. All parsers in a test must create the same result.

## Test plan
After a warm-up phase, all parsers are executed (repeatedly, if time permits) one after another for some minimum runtime on some given input. Runtime and memory usage are collected and logged. 

Before the next test cycle, the input size is increased by a given factor. This is done by wrapping multiple instances of each input file's content into JSON arrays. By default, a single flat top level array is used to do this, but there is an option to nest arrays deeply, i.e. wrapping multiple instances from the previous cycle's array into a new one.

Test cycles are repeated until eventually an OutOfMemoryError will occur, or until a parser requires more than twenty times the requested parse time. The default parsing time is 10 seconds, so if a parser needs 200 seconds or more, the test will stop.

Benchmark results are dumped to these files:
* `throughput.csv`
* `throughput.png`
* `memory.csv`
* `memory.png`

## Execution platforms

The benchmark covers two execution platforms:
* Java - parsers generated as Java code for direct invocation from Java.
* XQuery - parsers generated for use in XQuery, either generated as XQuery code, or generated as Java code for being used as an external function from XQuery, executed on [BaseX](https://basex.org/) or [SaxonJ-HE](https://www.saxonica.com/products/products.xml).


## Available parsers in Java

The result of all parsers for Java is a [com.fasterxml.jackson.databind.JsonNode](http://fasterxml.github.io/jackson-databind/javadoc/2.14/com/fasterxml/jackson/databind/JsonNode.html) from the [Jackson](https://github.com/FasterXML/jackson) project. The result object represents the parsed JSON. 

These parsers are available:

* `Jackson` - this is the reference implementation, using a [com.fasterxml.jackson.core.JsonParser](http://fasterxml.github.io/jackson-core/javadoc/2.14/com/fasterxml/jackson/core/JsonParser.html) as provided by the Jackson project itself, but using custom actions to create the result object.
* `HandCrafted` - a hand-crafted JSON parser.
* `REx_LL` - a REx-generated parser using the LL parsing algorithm.
* `REx_LALR` - a REx-generated parser using the LALR parsing algorithm.
* `JavaCC` - a [JavaCC](https://javacc.github.io/javacc/)-generated parser.
* `ANTRL4` - a parser generated by [ANTLR 4](https://www.antlr.org/).
* `Grammatica` - a parser generated by [Grammatica](https://grammatica.percederberg.net/).

## Available parsers in XQuery
The result of parsers for XQuery is an XML element as it would be produced by `fn:json-to-xml` (see definition in [XPath and XQuery 3.1 Functions and Operators](https://www.w3.org/TR/xpath-functions-31/#func-json-to-xml)).

* `BaseX` - a reference implementation, directly using `fn:json-to-xml`, executed on BaseX.
* `BaseXRExLL` - a REx-generated parser using the LL parsing algorithm, executed on BaseX.
* `BaseXRExLALR` - a REx-generated parser using the LALR parsing algorithm, executed on BaseX.
* `BaseXRExLLExternal` - a REx-generated parser using the LL parsing algorithm in an external function, executed on BaseX.
* `BaseXRExLLExternal` - a REx-generated parser using the LALR parsing algorithm in an external function, executed on BaseX.
* `BaseXIxml` - a parser using [Invisible XML](https://invisiblexml.org/), implemented by [nineml.org](https://github.com/nineml) projects, executed on BaseX.
* `SaxonX` - a reference implementation, directly using `fn:json-to-xml` executed on SaxonJ-HE.
* `SaxonXRExLL` - a REx-generated parser using the LL parsing algorithm, executed on SaxonJ-HE.
* `SaxonXRExLALR` - a REx-generated parser using the LALR parsing algorithm, executed on SaxonJ-HE.
* `SaxonXRExLLExternal` - a REx-generated parser using the LL parsing algorithm in an external function, executed on SaxonJ-HE.
* `SaxonXRExLLExternal` - a REx-generated parser using the LALR parsing algorithm in an external function, executed on SaxonJ-HE.
* `SaxonXIxml` - a parser using [Invisible XML](https://invisiblexml.org/), implemented by [nineml.org](https://github.com/nineml) projects, executed on SaxonJ-HE.

## Building rex-parser-benchmark

Use Java 11 or higher to build.

This project makes use of `org.nineml:coffeesacks:1.99.14`. As long as this has not been released (see [CoffeeSacks releases](https://github.com/nineml/coffeesacks/releases)), it can be built locally by

```sh
git clone https://github.com/nineml/coffeesacks.git
cd coffeesacks
gradlew publishToMavenLocal
```

This may fail with

`Cannot perform signing task ':signMavenJavaPublication' because it has no configured signatory`

But this can be avoided by commenting these three lines in `build.gradle` of the `coffeesacks` project:

```gradle
signing {
  sign publishing.publications
}
```

Then `rex-parser-benchmark` can be built by

```sh
git clone https://github.com/GuntherRademacher/rex-parser-benchmark.git
cd rex-parser-benchmark 
gradlew build
```

After the project has been built with Gradle, it can also be imported into Eclipse.

## Running rex-parser-benchmark

The benchmark can be run with the `run` task:

```sh
gradlew run
```

The above command uses all defaults, it will run the Java parsers.

There are a number of command line options. These are shown when passing `-?` as an argument:

```sh
gradlew run "--args=-?"
```

This results in:

```log
Usage: java Benchmark <OPTION>... [<FILE>|<DIRECTORY>]

  read JSON file, or all *.json files in given directory (default: current dir). Restrict
  to those that are parseable by all parsers. Parse repeatedly. Log execution time.

  Options:

  -?, --help               show this message
  --platform [java|xquery] use Java or XQuery parser set
                             (default: java)
  --exclude <PARSER>       exclude <PARSER> from test
  --include <PARSER>       include <PARSER> in test
  --novalidation           skip comparison of parsing results
  --create-result          create result JsonObject (Java only,
                             XQuery always is executed with results)
  --warmup <TIME>          warm up parsers for <TIME> seconds
                             (default: 10)
  --time <TIME>            run each parser for <TIME> seconds
                             (default: 10)
  --factor <FACTOR>        increase input size by <FACTOR>
                             after each test cycle (default 2)
  --nest                   nest JSON arrays, when increasing input size. By
                             default, a single top level array will be used.
```

So for running the XQuery benchmark on file `src/main/resources/8KB.json`, use this command:

```sh
gradlew run --args="--platform xquery src/main/resources/8KB.json"
```

## Benchmark results - Java

![throughput-java](throughput-java.png)
![memory-java](memory-java.png)

## Benchmark results - XQuery

![throughput-xquery](throughput-xquery.png)
![memory-xquery](memory-xquery.png)

## License

This project is subject to the [Apache 2 License][ASL].

[ASL]: http://www.apache.org/licenses/LICENSE-2.0