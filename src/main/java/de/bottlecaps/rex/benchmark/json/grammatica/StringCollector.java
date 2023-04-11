package de.bottlecaps.rex.benchmark.json.grammatica;

import net.percederberg.grammatica.parser.Node;
import net.percederberg.grammatica.parser.ParseException;
import net.percederberg.grammatica.parser.Production;
import net.percederberg.grammatica.parser.Token;

public class StringCollector extends StringAnalyzer {
  private StringBuilder sb = new StringBuilder();

  public String getValue() {
    return sb.toString();
  }

  @Override
  protected Node exit(Node node) throws ParseException {
    super.exit(node);
    return null;
  }

  @Override
  protected void enterString(Production node) throws ParseException {
    sb.setLength(0);
  }

  @Override
  protected Node exitCharacters(Token node) throws ParseException {
    sb.append(node.getImage());
    return node;
  }

  @Override
  protected Node exitEscapeQuote(Token node) throws ParseException {
    sb.append("\"");
    return node;
  }

  @Override
  protected Node exitEscapeBackslash(Token node) throws ParseException {
    sb.append("\\");
    return node;
  }

  @Override
  protected Node exitEscapeSlash(Token node) throws ParseException {
    sb.append("/");
    return node;
  }

  @Override
  protected Node exitEscapeB(Token node) throws ParseException {
    sb.append("\b");
    return node;
  }

  @Override
  protected Node exitEscapeF(Token node) throws ParseException {
    sb.append("\f");
    return node;
  }

  @Override
  protected Node exitEscapeN(Token node) throws ParseException {
    sb.append("\n");
    return node;
  }

  @Override
  protected Node exitEscapeR(Token node) throws ParseException {
    sb.append("\r");
    return node;
  }

  @Override
  protected Node exitEscapeT(Token node) throws ParseException {
    sb.append("\t");
    return node;
  }

  @Override
  protected Node exitEscapeU(Token node) throws ParseException {
    sb.append(unhex(node.getImage().substring(2)));
    return node;
  }

  private String unhex(String value) {
    return Character.valueOf((char) Integer.parseInt(value, 16)).toString();
  }
}