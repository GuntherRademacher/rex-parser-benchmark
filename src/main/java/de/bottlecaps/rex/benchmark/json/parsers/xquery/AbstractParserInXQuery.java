package de.bottlecaps.rex.benchmark.json.parsers.xquery;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.bottlecaps.rex.benchmark.json.parsers.AbstractJsonParser;

public abstract class AbstractParserInXQuery extends AbstractJsonParser {
  protected final String query;

  public AbstractParserInXQuery(String filename) {
    String resource = getClass().getPackageName().replace('.', '/') + "/" + filename;
    URI resourceURI;
    try {
      resourceURI = Thread.currentThread().getContextClassLoader().getResource(resource).toURI();
      this.query = Files.readString(Paths.get(resourceURI));
    }
    catch (URISyntaxException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String getQuery() {
    return query;
  }
}
