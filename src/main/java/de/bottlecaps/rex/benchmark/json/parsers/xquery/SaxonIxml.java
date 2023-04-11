package de.bottlecaps.rex.benchmark.json.parsers.xquery;

import org.nineml.coffeesacks.RegisterCoffeeSacks;

public class SaxonIxml extends AbstractSaxonParser {

  public SaxonIxml() {
    super("saxon-ixml.xq", new RegisterCoffeeSacks());
  }
}