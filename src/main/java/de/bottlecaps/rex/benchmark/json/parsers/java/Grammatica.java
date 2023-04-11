package de.bottlecaps.rex.benchmark.json.parsers.java;

import java.io.StringReader;

import com.fasterxml.jackson.databind.JsonNode;

import de.bottlecaps.rex.benchmark.json.grammatica.JsonCollector;
import de.bottlecaps.rex.benchmark.json.parsers.AbstractJsonParser;

public class Grammatica extends AbstractJsonParser {
  @Override
  public JsonNode parse(String input) throws Exception {
    JsonCollector jsonCollector = new JsonCollector();
    net.percederberg.grammatica.parser.Parser parser =
      new de.bottlecaps.rex.benchmark.json.grammatica.JSONParser(new StringReader(input), jsonCollector);
    parser.parse();
      return jsonCollector.getValue();
  }
}