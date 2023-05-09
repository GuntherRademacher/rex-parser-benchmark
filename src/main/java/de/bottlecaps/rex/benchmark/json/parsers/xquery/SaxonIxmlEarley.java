package de.bottlecaps.rex.benchmark.json.parsers.xquery;

import org.nineml.coffeesacks.RegisterCoffeeSacks;

public class SaxonIxmlEarley extends AbstractSaxonParser {

  public SaxonIxmlEarley() {
    super("saxon-ixml-earley.xq", new RegisterCoffeeSacks());
  }
}