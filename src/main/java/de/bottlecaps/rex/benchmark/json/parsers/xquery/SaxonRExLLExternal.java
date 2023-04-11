package de.bottlecaps.rex.benchmark.json.parsers.xquery;

public class SaxonRExLLExternal extends AbstractSaxonParser {
  public SaxonRExLLExternal() {
    super("rex-ll-external.xq", new RExLL.SaxonInitializer());
  }
}