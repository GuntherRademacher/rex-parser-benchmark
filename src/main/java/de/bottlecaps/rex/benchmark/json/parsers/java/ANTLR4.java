package de.bottlecaps.rex.benchmark.json.parsers.java;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.UnbufferedTokenStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import com.fasterxml.jackson.databind.JsonNode;

import de.bottlecaps.rex.benchmark.json.antlr4.JSONLexer;
import de.bottlecaps.rex.benchmark.json.antlr4.JSONParser;
import de.bottlecaps.rex.benchmark.json.parsers.AbstractJsonParser;

public class ANTLR4 extends AbstractJsonParser {
  @Override
  public JsonNode parse(String input) throws Exception {
    JSONLexer lexer = new JSONLexer(CharStreams.fromString(input));
    JSONParser parser = new JSONParser(new UnbufferedTokenStream<>(lexer));
    parser.setBuildParseTree(false);

    List<String> messages = new ArrayList<>();
    ANTLRErrorListener listener = new ANTLRErrorListener() {
      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        messages.add("line " + line + " column " + (charPositionInLine + 1) + ": " + msg);
      }

      @Override
      public void reportAmbiguity(org.antlr.v4.runtime.Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void reportAttemptingFullContext(org.antlr.v4.runtime.Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void reportContextSensitivity(org.antlr.v4.runtime.Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
        throw new UnsupportedOperationException();
      }
    };
    lexer.removeErrorListeners();
    parser.removeErrorListeners();
    lexer.addErrorListener(listener);
    parser.addErrorListener(listener);
    parser.json();
    if (! messages.isEmpty())
      throw new RuntimeException(messages.stream().collect(Collectors.joining("\n")));
    return parser.getValue();
  }
}