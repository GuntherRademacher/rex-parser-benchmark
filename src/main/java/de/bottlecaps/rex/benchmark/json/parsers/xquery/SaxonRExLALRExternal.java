package de.bottlecaps.rex.benchmark.json.parsers.xquery;

public class SaxonRExLALRExternal extends AbstractSaxonParser {
  public SaxonRExLALRExternal() {
    super("rex-lalr-external.xq", new RExLL.SaxonInitializer());
  }
}