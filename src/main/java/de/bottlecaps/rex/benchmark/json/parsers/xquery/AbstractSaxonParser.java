package de.bottlecaps.rex.benchmark.json.parsers.xquery;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import net.sf.saxon.Configuration;
import net.sf.saxon.lib.Initializer;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmAtomicValue;

public abstract class AbstractSaxonParser extends AbstractParserInXQuery {
  private XQueryExecutable executable;
  private Processor processor;

  public AbstractSaxonParser(String filename) {
    this(filename, null);
  }

  public AbstractSaxonParser(String filename, Initializer initializer) {
    super(filename);
    Configuration configuration = new Configuration();
    if (initializer != null)
      try {
        initializer.initialize(configuration);
      }
      catch (Exception e) {
        throw new RuntimeException(e);
      }
    processor = new Processor(configuration);
    configuration.setProcessor(processor);
    XQueryCompiler compiler = processor.newXQueryCompiler();
    try {
      executable = compiler.compile(query);
    }
    catch (SaxonApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String parse(String input) throws Exception {
    XQueryEvaluator evaluator = executable.load();
    evaluator.setExternalVariable(new QName("input"), new XdmAtomicValue(input));
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      Serializer serializer = processor.newSerializer(outputStream);
      serializer.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
      evaluator.run(serializer);
      return new String(outputStream.toByteArray(), StandardCharsets.UTF_8)
          .replace("&#xD;", "")
          .replace("\r", "");
    }
  }
}