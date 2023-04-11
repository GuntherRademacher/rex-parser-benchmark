package de.bottlecaps.rex.benchmark.json.parsers.java;

import java.io.StringReader;

import com.fasterxml.jackson.databind.JsonNode;

import de.bottlecaps.rex.benchmark.json.parsers.AbstractJsonParser;

public class JavaCC extends AbstractJsonParser {
  @Override
  public JsonNode parse(String input) throws Exception {
    de.bottlecaps.rex.benchmark.json.javacc.JSON parser = new de.bottlecaps.rex.benchmark.json.javacc.JSON(new StringReader(input));
    parser.json();
    return parser.getValue();
  }
}