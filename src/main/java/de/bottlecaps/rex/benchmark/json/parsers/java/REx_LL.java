package de.bottlecaps.rex.benchmark.json.parsers.java;

import com.fasterxml.jackson.databind.JsonNode;

import de.bottlecaps.rex.benchmark.json.parsers.AbstractJsonParser;
import de.bottlecaps.rex.benchmark.json.rex.JSON_LL;

public class REx_LL extends AbstractJsonParser {
  @Override
  public JsonNode parse(String input) throws Exception {
    JSON_LL parser = new JSON_LL();
    parser.initialize(input);
    try {
      parser.parse_json();
    }
    catch (JSON_LL.ParseException pe) {
      throw new RuntimeException(parser.getErrorMessage(pe));
    }
    return parser.getValue();
  }
}