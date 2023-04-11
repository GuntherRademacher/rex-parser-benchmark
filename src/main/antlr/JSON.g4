                                                            grammar JSON;

                                                            options {tokenVocab=JSONLexer;}

                                                            @header {
                                                              package de.bottlecaps.rex.benchmark.json.antlr4;

                                                              import com.fasterxml.jackson.databind.JsonNode;
                                                              import com.fasterxml.jackson.databind.node.ArrayNode;
                                                              import com.fasterxml.jackson.databind.node.JsonNodeFactory;
                                                              import com.fasterxml.jackson.databind.node.ObjectNode;
                                                              import com.fasterxml.jackson.databind.node.TextNode;
                                                              import de.bottlecaps.rex.benchmark.json.parsers.ResultFactory;
                                                              import java.util.Stack;
                                                            }

                                                            @members {
                                                              StringBuilder sb = new StringBuilder();

                                                              private JsonNode value;
                                                              private JsonNodeFactory factory = ResultFactory.getJsonNodeFactory();
                                                              private Stack<ArrayNode> arrays = new Stack<>();
                                                              private Stack<ObjectNode> objects = new Stack<>();

                                                              public JsonNode getValue() {
                                                                return value;
                                                              }
                                                            }

json       : value EOF ;
value      : object                                         {value = objects.pop();}
           | array                                          {value = arrays.pop();}
           | string                                         {value = factory.textNode(sb.toString());}
           | INT                                            {
                                                              try {
                                                                value = factory.numberNode(Integer.parseInt($INT.text));
                                                              }
                                                              catch (NumberFormatException e) {
                                                                value = factory.numberNode(Long.parseLong($INT.text));
                                                              }
                                                            }
           | DOUBLE                                         {value = factory.numberNode(Double.parseDouble($DOUBLE.text));}
           | TRUE                                           {value = factory.booleanNode(true);}
           | FALSE                                          {value = factory.booleanNode(false);}
           | NULL                                           {value = factory.nullNode();}
           ;
object     :                                                {objects.push(factory.objectNode());}
             LBRACE ( member ( COMMA member )* )? RBRACE ;
member     : string                                         {String key = sb.toString();}
                    COLON value                             {objects.peek().set(key, value);}
           ;
array      :                                                {arrays.push(factory.arrayNode());}
             LBRACKET ( element ( COMMA element )* )? RBRACKET ;
element    : value                                          {arrays.peek().add(value);}
           ;
string     :                                                {sb.setLength(0);}
             BEGIN_STRING
             ( CHARACTERS                                   {sb.append($CHARACTERS.text);}
                          | ESCAPE                          {sb.append($ESCAPE.text);}
                                   )*
             END_STRING
           ;
