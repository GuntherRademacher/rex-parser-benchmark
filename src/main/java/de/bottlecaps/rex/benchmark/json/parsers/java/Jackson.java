package de.bottlecaps.rex.benchmark.json.parsers.java;

import java.util.Stack;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.bottlecaps.rex.benchmark.json.parsers.AbstractJsonParser;
import de.bottlecaps.rex.benchmark.json.parsers.ResultFactory;

public class Jackson  extends AbstractJsonParser {
  private JsonFactory jsonFactory = new JsonFactory();

  @Override
  public JsonNode parse(String input) throws Exception {
    com.fasterxml.jackson.core.JsonParser jsonParser = jsonFactory.createParser(input);
    JsonNodeFactory jsonNodeFactory = ResultFactory.getJsonNodeFactory();
      JsonNode value = null;
      Stack<ContainerNode<?>> containers = new Stack<>();
      Stack<String> keys = new Stack<>();
    for (;;) {
      value = null;
      switch (jsonParser.nextToken()) {
      case END_ARRAY:
        value = containers.pop();
        break;
      case END_OBJECT:
        value = containers.pop();
        break;
      case FIELD_NAME:
        keys.push(jsonParser.getText());
        break;
      case START_ARRAY:
        containers.push(jsonNodeFactory.arrayNode());
        break;
      case START_OBJECT:
        containers.push(jsonNodeFactory.objectNode());
        break;
      case VALUE_FALSE:
        value = jsonNodeFactory.booleanNode(false);
        break;
      case VALUE_NULL:
        value = jsonNodeFactory.nullNode();
        break;
      case VALUE_NUMBER_FLOAT:
        value = jsonNodeFactory.numberNode(Double.parseDouble(jsonParser.getText()));
        break;
      case VALUE_NUMBER_INT:
        try {
          value = jsonNodeFactory.numberNode(Integer.parseInt(jsonParser.getText()));
        }
        catch (NumberFormatException e) {
          value = jsonNodeFactory.numberNode(Long.parseLong(jsonParser.getText()));
        }
        break;
      case VALUE_STRING:
        value = jsonNodeFactory.textNode(jsonParser.getText());
        break;
      case VALUE_TRUE:
        value = jsonNodeFactory.booleanNode(true);
        break;
      default:
        throw new IllegalStateException("unsupported JsonToken: " + jsonParser.currentToken());
      }
      if (value != null) {
        if (containers.isEmpty())
          break;
        ContainerNode<?> container = containers.peek();
        if (container.isArray())
          ((ArrayNode) container).add(value);
        else
          ((ObjectNode) container).set(keys.pop(), value);
      }
    }
    return value;
  }
}