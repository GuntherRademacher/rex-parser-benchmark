package de.bottlecaps.rex.benchmark.json.parsers.xquery;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import org.basex.core.Context;
import org.basex.io.serial.Serializer;
import org.basex.io.serial.SerializerOptions;
import org.basex.query.QueryProcessor;
import org.basex.query.iter.BasicIter;
import org.basex.query.value.Value;
import org.basex.query.value.item.Item;

public abstract class AbstractBaseXParser extends AbstractParserInXQuery {
  private static Context context = new Context();
  private static SerializerOptions serializerOptions = new SerializerOptions();

  public AbstractBaseXParser(String filename) {
    super(filename);
  }

  @Override
  public String parse(String input) throws Exception {
    try (QueryProcessor queryProcessor = new QueryProcessor(query, context)) {
      queryProcessor.variable("input", input);
      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
           Serializer serializer = Serializer.get(outputStream, serializerOptions)) {
        Value result = queryProcessor.iter().value(queryProcessor.qc, null);
        BasicIter<Item> iter = result.iter();
        for (Item item; (item = iter.next()) != null;)
          serializer.serialize(item);
        return new String(outputStream.toByteArray(), StandardCharsets.UTF_8)
            .replace("\r", "");
      }
    }
  }
}