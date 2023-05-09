package de.bottlecaps.rex.benchmark.json.parsers.xquery;

import org.nineml.coffeesacks.RegisterCoffeeSacks;

public class SaxonIxmlGLL extends AbstractSaxonParser {

  public SaxonIxmlGLL() {
    super("saxon-ixml-gll.xq", new RegisterCoffeeSacks());
  }
}