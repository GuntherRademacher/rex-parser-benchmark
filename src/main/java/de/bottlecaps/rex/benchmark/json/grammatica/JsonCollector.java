package de.bottlecaps.rex.benchmark.json.grammatica;

import java.util.Stack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import de.bottlecaps.rex.benchmark.json.parsers.ResultFactory;
import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.Token;

public class JsonCollector extends JSONAnalyzer {
  private JsonNodeFactory factory = ResultFactory.getJsonNodeFactory();
  private JsonNode value;
  private Stack<ArrayNode> arrays = new Stack<>();
  private Stack<ObjectNode> objects = new Stack<>();
  private Stack<String> keys = new Stack<>();

  public JsonNode getValue() {
    return value;
  }

  @Override
  protected Node exit(Node node) throws ParseException {
    super.exit(node);
    return null;
  }

  @Override
  protected Node exitObject(Production node) throws ParseException {
    value = objects.pop();
    return node;
  }

  @Override
  protected Node exitArray(Production node) throws ParseException {
    value = arrays.pop();
    return node;
  }

  @Override
  protected Node exitString(Token node) throws ParseException {
    String string = node.getImage();
    string = string.substring(1, string.length() - 1);
    if (string.contains("\\")) {
      StringBuilder sb = new StringBuilder();
      boolean skip = true;
      for (String fragment : string.split("\\\\", -1)) {
        if (skip) {
          sb.append(fragment);
          skip = false;
        }
        else if (fragment.length() == 0) {
          sb.append('\\');
          skip = true;
        }
        else {
          switch (fragment.charAt(0)) {
          case '"':
            sb.append('"');
            sb.append(fragment.substring(1));
            break;
          case '/':
            sb.append('/');
            sb.append(fragment.substring(1));
            break;
          case 'b':
            sb.append('\b');
            sb.append(fragment.substring(1));
            break;
          case 'f':
            sb.append('\f');
            sb.append(fragment.substring(1));
            break;
          case 'n':
            sb.append('\n');
            sb.append(fragment.substring(1));
            break;
          case 'r':
            sb.append('\r');
            sb.append(fragment.substring(1));
            break;
          case 't':
            sb.append('\t');
            sb.append(fragment.substring(1));
            break;
          case 'u':
            sb.append(unhex(fragment.substring(1, 5)));
            sb.append(fragment.substring(5));
            break;
          default:
            throw new IllegalStateException("unsupported escape sequence");
          }
          skip = false;
        }
      }
      string = sb.toString();
    }
    value = factory.textNode(string);
    return node;
  }

  private static String unhex(String value) {
    return Character.valueOf((char) Integer.parseInt(value, 16)).toString();
  }

  @Override
  protected Node exitInt(Token node) throws ParseException {
    try {
      value = factory.numberNode(Integer.parseInt(node.getImage()));
    }
    catch (NumberFormatException e) {
      value = factory.numberNode(Long.parseLong(node.getImage()));
    }
    return node;
  }

  @Override
  protected Node exitDouble(Token node) throws ParseException {
    value = factory.numberNode(Double.parseDouble(node.getImage()));
    return node;
  }

  @Override
  protected Node exitTrue(Token node) throws ParseException {
    value = factory.booleanNode(true);
    return node;
  }

  @Override
  protected Node exitFalse(Token node) throws ParseException {
    value = factory.booleanNode(false);
    return node;
  }

  @Override
  protected Node exitNull(Token node) throws ParseException {
    value = factory.nullNode();
    return node;
  }

  @Override
  protected Node exitKey(Production node) throws ParseException {
    keys.push(((TextNode) value).asText());
    return node;
  }

  @Override
  protected Node exitMember(Production node) throws ParseException {
    objects.peek().set(keys.pop(), value);
    return node;
  }

  @Override
  protected Node exitElement(Production node) throws ParseException {
    arrays.peek().add(value);
    return node;
  }

  @Override
  protected void enterObject(Production node) throws ParseException {
    objects.push(factory.objectNode());
  }

  @Override
  protected void enterArray(Production node) throws ParseException {
    arrays.push(factory.arrayNode());
  }
}
