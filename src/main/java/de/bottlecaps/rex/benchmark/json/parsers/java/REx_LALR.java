package de.bottlecaps.rex.benchmark.json.parsers.java;

import com.fasterxml.jackson.databind.JsonNode;

import de.bottlecaps.rex.benchmark.json.parsers.AbstractJsonParser;
import de.bottlecaps.rex.benchmark.json.rex.JSON_LALR;

public class REx_LALR extends AbstractJsonParser {
  @Override
  public JsonNode parse(String input) throws Exception {
    JSON_LALR parser = new JSON_LALR();
    parser.initialize(input);
    try {
      parser.parse_json();
    }
    catch (JSON_LALR.ParseException pe) {
      throw new RuntimeException(parser.getErrorMessage(pe));
    }
    return parser.getValue();
  }
}