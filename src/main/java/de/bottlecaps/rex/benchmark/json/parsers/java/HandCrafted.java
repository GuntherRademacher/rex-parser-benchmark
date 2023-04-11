package de.bottlecaps.rex.benchmark.json.parsers.java;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.bottlecaps.rex.benchmark.json.parsers.AbstractJsonParser;
import de.bottlecaps.rex.benchmark.json.parsers.ResultFactory;

public class HandCrafted extends AbstractJsonParser {
  private HandCraftedJsonLexer lexer;
  private JsonNodeFactory factory;

  @Override
  public JsonNode parse(String input) throws Exception {
    lexer = new HandCraftedJsonLexer(input);
    factory = ResultFactory.getJsonNodeFactory();
    JsonNode value = parseValue(lexer.nextToken());
    if (lexer.nextToken() != Token.EOF)
      error();
    return value;
  }

  private void error() {
    throw new IllegalStateException();
  }

  private JsonNode parseValue(Token currentToken) {
    switch (currentToken) {
    case LEFT_BRACE:
      return parseObject();
    case LEFT_BRACKET:
      return parseArray();
    case INTEGER:
      return factory.numberNode(lexer.intValue());
    case LONG:
      return factory.numberNode(lexer.longValue());
    case DOUBLE:
      return factory.numberNode(lexer.doubleValue());
    case STRING:
      return factory.textNode(lexer.stringValue());
    case TRUE:
      return factory.booleanNode(true);
    case FALSE:
      return factory.booleanNode(false);
    case NULL:
      return factory.nullNode();
    default:
      error();
      return null;
    }
  }

  private ArrayNode parseArray() {
    ArrayNode array = factory.arrayNode();
    Token currentToken = lexer.nextToken();
    if (currentToken != Token.RIGHT_BRACKET) {
      for (;;) {
        array.add(parseValue(currentToken));
        if (lexer.nextTokenCommaOrRightBracket() != Token.COMMA)
          break;
        currentToken = lexer.nextToken();
      }
    }
    return array;
  }

  private ObjectNode parseObject() {
    ObjectNode object = factory.objectNode();
    Token currentToken = lexer.nextTokenStringOrRightBrace();
    if (currentToken != Token.RIGHT_BRACE) {
      for (;;) {
        String key = lexer.stringValue();
        lexer.nextTokenColon();
        object.set(key, parseValue(lexer.nextToken()));
        if (lexer.nextTokenCommaOrRightBrace() != Token.COMMA)
          break;
        lexer.nextTokenString();
      }
    }
    return object;
  }

  private static class HandCraftedJsonLexer {
    private String input;
    private int size;
    private int index;
    private StringBuilder stringBuilder;
    private String stringValue;
    private int intValue;
    private long longValue;
    private double doubleValue;

    public HandCraftedJsonLexer(String input) {
      this.input = input;
      this.size = input.length();
      this.index = 0;
      this.stringBuilder = new StringBuilder();
    }

    public String stringValue() {
      return stringValue;
    }

    public int intValue() {
      return intValue;
    }

    public long longValue() {
      return longValue;
    }

    public double doubleValue() {
      return doubleValue;
    }

    public Token nextToken() {
      switch (nextNonWhitespaceChar()) {
      case -1:
        return Token.EOF;
      case '"':
        return string();
      case '-':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        return number();
      case '{':
        return Token.LEFT_BRACE;
      case '[':
        return Token.LEFT_BRACKET;
      case '}':
        return Token.RIGHT_BRACE;
      case ']':
        return Token.RIGHT_BRACKET;
      case 'n':
        return nullToken();
      case 't':
        return trueToken();
      case 'f':
        return falseToken();
      default:
        error();
        return null;
      }
    }

    public void nextTokenString() {
      if (nextNonWhitespaceChar() == '"')
        string();
      else
        error();
    }

    public Token nextTokenStringOrRightBrace() {
      switch (nextNonWhitespaceChar()) {
      case '"':
        return string();
      case '}':
        return Token.RIGHT_BRACE;
      default:
        error();
        return null;
      }
    }

    public void nextTokenColon() {
      if (nextNonWhitespaceChar() != ':')
        error();
    }

    public Token nextTokenCommaOrRightBrace() {
      switch (nextNonWhitespaceChar()) {
      case ',':
        return Token.COMMA;
      case '}':
        return Token.RIGHT_BRACE;
      default:
        error();
        return null;
      }
    }

    public Token nextTokenCommaOrRightBracket() {
      switch (nextNonWhitespaceChar()) {
      case ',':
        return Token.COMMA;
      case ']':
        return Token.RIGHT_BRACKET;
      default:
        error();
        return null;
      }
    }

    private int nextChar() {
      return index < size ? input.charAt(index++) : -1;
    }

    private void nextChar(char expectedChar) {
      if (expectedChar != nextChar())
        error();
    }

    private int nextNonWhitespaceChar() {
      for (;;) {
        int c = nextChar();
        if (c > ' ')
          return c;
        if (" \t\n\r".indexOf(c) >= 0)
          continue;
        if (c == -1)
          return c;
        error();
        return 0;
      }
    }

    Token nullToken() {
      nextChar('u');
      nextChar('l');
      nextChar('l');
      return Token.NULL;
    }

    Token falseToken() {
      nextChar('a');
      nextChar('l');
      nextChar('s');
      nextChar('e');
      return Token.FALSE;
    }

    private Token trueToken() {
      nextChar('r');
      nextChar('u');
      nextChar('e');
      return Token.TRUE;
    }

    private void error() {
      throw new IllegalStateException();
    }

    private Token string() {
      int end = input.indexOf('"', index);
      if (end < 0)
        error();
      stringValue = input.substring(index, end);
      if (stringValue.indexOf('\\') < 0) {
        for (int i = 0; i < stringValue.length(); ++i)
          if (stringValue.charAt(i) < ' ')
            error();
        index = end + 1;
        return Token.STRING;
      }
      stringBuilder.setLength(0);
      int begin = index;
      for (;;) {
        int c = nextChar();
        switch (c) {
        case '"':
          stringBuilder.append(input.substring(begin, index - 1));
          stringValue = stringBuilder.toString();
          return Token.STRING;
        case '\\':
          stringBuilder.append(input.substring(begin, index - 1));
          begin = index + 1;
          switch (nextChar()) {
          case -1:
            error();
            return null;
          case '"':
            stringBuilder.append('"');
            continue;
          case '\\':
            stringBuilder.append('\\');
            continue;
          case '/':
            stringBuilder.append('/');
            continue;
          case 'b':
            stringBuilder.append('\b');
            continue;
          case 'f':
            stringBuilder.append('\f');
            continue;
          case 'n':
            stringBuilder.append('\n');
            continue;
          case 'r':
            stringBuilder.append('\r');
            continue;
          case 't':
            stringBuilder.append('\t');
            continue;
          case 'u':
            if (index + 4 > size)
              error();
            try {
              stringBuilder.append(unhex(input.substring(index, index + 4)));
            }
            catch (NumberFormatException e) {
              error();
            }
            index += 4;
            begin = index;
            continue;
          default:
          }
          error();
          return null;
        case -1:
          error();
          return null;
        default:
          if (c < ' ')
            error();
        }
      }
    }

    private String unhex(String value) {
      return Character.valueOf((char) Integer.parseInt(value, 16)).toString();
    }

    private Token number() {
      int begin = index - 1;
      int end = index;
      for (;;) {
        switch (nextChar()) {
        case '0':
//        if (c == '-' && begin == index - 2)
//          error();
//        continue;
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          continue;
        case '.':
          return fraction(begin);
        case 'e':
        case 'E':
          return exponent(begin);
        case ' ':
        case '\t':
        case '\n':
        case '\r':
          end = index - 1;
          break;
        default:
          end = --index;
          break;
        }
        break;
      }
      try {
        intValue = Integer.parseInt(input.substring(begin, end));
        return Token.INTEGER;
      }
      catch (NumberFormatException e) {
        longValue = Long.parseLong(input.substring(begin, end));
        return Token.LONG;
      }
    }

    private Token fraction(int begin) {
      int end = begin;
      for (;;) {
        switch (nextChar()) {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          continue;
        case 'e':
        case 'E':
          return exponent(begin);
        case ' ':
        case '\t':
        case '\n':
        case '\r':
          end = index - 1;
          break;
        default:
          end = --index;
          break;
        }
        break;
      }
      doubleValue = Double.parseDouble(input.substring(begin, end));
      return Token.DOUBLE;
    }

    private Token exponent(int begin) {
      int exponentBegin = index;
      int end = begin;
      for (;;) {
        switch (nextChar()) {
        case '+':
        case '-':
          if (exponentBegin != index - 1)
            error();
          continue;
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          continue;
        case ' ':
        case '\t':
        case '\n':
        case '\r':
          end = index - 1;
          break;
        default:
          end = --index;
          break;
        }
        break;
      }
      doubleValue = Double.parseDouble(input.substring(begin, end));
      return Token.DOUBLE;
    }
  }

  private enum Token {
    STRING, INTEGER, LONG, DOUBLE, TRUE, FALSE, NULL, LEFT_BRACE, RIGHT_BRACE, LEFT_BRACKET, RIGHT_BRACKET, COMMA,
    COLON, EOF
  }
}
