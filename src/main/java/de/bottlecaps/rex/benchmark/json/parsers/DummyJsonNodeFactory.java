package de.bottlecaps.rex.benchmark.json.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

public class DummyJsonNodeFactory extends JsonNodeFactory {
  public static final DummyJsonNodeFactory instance = new DummyJsonNodeFactory();

  private static final long serialVersionUID = 73558970497494358L;
  private static final TextNode dummyTextNode = new TextNode("");
  private static final NumericNode dummyNumericNode = new IntNode(0);
  private static final BooleanNode dummyBooleanNode = BooleanNode.FALSE;
  private static final NullNode dummyNullNode = NullNode.instance;

  @SuppressWarnings({ "all", "unchecked" })
  private static final ObjectNode dummyObjectNode = new ObjectNode(instance) {
    @Override
    public JsonNode set(String fieldName, JsonNode value) {
      return this;
    }
  };

  @SuppressWarnings({ "all", "unchecked" })
  private static final ArrayNode dummyArrayNode = new ArrayNode(instance) {
    @Override
    public ArrayNode add(JsonNode value) {
      return this;
    }
  };

  private DummyJsonNodeFactory() {
  }

  @Override
  public TextNode textNode(String value) {
    return dummyTextNode;
  }

  @Override
  public NumericNode numberNode(int value) {
    return dummyNumericNode;
  }

  @Override
  public NumericNode numberNode(double value) {
    return dummyNumericNode;
  }

  @Override
  public BooleanNode booleanNode(boolean v) {
    return dummyBooleanNode;
  }

  @Override
  public NullNode nullNode() {
    return dummyNullNode;
  }

  @Override
  public ObjectNode objectNode() {
    return dummyObjectNode;
  }

  @Override
  public ArrayNode arrayNode() {
    return dummyArrayNode;
  }
}