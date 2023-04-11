package de.bottlecaps.rex.benchmark.json.parsers;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class ResultFactory {
  private static JsonNodeFactory factory = null;

  public static JsonNodeFactory getJsonNodeFactory() {
    return factory;
  }

  public static void setJsonNodeFactory(JsonNodeFactory factory) {
    ResultFactory.factory = factory;
  }
}