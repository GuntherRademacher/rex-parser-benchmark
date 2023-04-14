// This file was generated on Fri Apr 14, 2023 08:38 (UTC+02) by REx v5.57 which is Copyright (c) 1979-2023 by Gunther Rademacher <grd@gmx.net>
// REx command line: ../../../../../../rex/size.ebnf -java -a java -name de.bottlecaps.rex.benchmark.json.Size

                                                            // line 2 "../../../../../../rex/size.ebnf"
                                                            package de.bottlecaps.rex.benchmark.json;

                                                            public class Size
                                                            {
                                                              private double value;

                                                              private Size(CharSequence string)
                                                              {
                                                                initialize(string);
                                                              }

                                                              public static long valueOf(String string)
                                                              {
                                                                Size parser = new Size(string);
                                                                try
                                                                {
                                                                  parser.parse_size();
                                                                }
                                                                catch (ParseException e)
                                                                {
                                                                  throw new RuntimeException(parser.getErrorMessage(e), e);
                                                                }
                                                                return (long) parser.value;
                                                              }
                                                            // line 30 "Size.java"

  public static class ParseException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;
    private int begin, end, offending, expected, state;

    public ParseException(int b, int e, int s, int o, int x)
    {
      begin = b;
      end = e;
      state = s;
      offending = o;
      expected = x;
    }

    @Override
    public String getMessage()
    {
      return offending < 0
           ? "lexical analysis failed"
           : "syntax error";
    }

    public int getBegin() {return begin;}
    public int getEnd() {return end;}
    public int getState() {return state;}
    public int getOffending() {return offending;}
    public int getExpected() {return expected;}
    public boolean isAmbiguousInput() {return false;}
  }

  public void initialize(CharSequence source)
  {
    input = source;
    size = source.length();
    reset(0, 0, 0);
  }

  public CharSequence getInput()
  {
    return input;
  }

  public int getTokenOffset()
  {
    return b0;
  }

  public int getTokenEnd()
  {
    return e0;
  }

  public final void reset(int l, int b, int e)
  {
            b0 = b; e0 = b;
    l1 = l; b1 = b; e1 = e;
    end = e;
  }

  public void reset()
  {
    reset(0, 0, 0);
  }

  public static String getOffendingToken(ParseException e)
  {
    return e.getOffending() < 0 ? null : TOKEN[e.getOffending()];
  }

  public static String[] getExpectedTokenSet(ParseException e)
  {
    String[] expected;
    if (e.getExpected() >= 0)
    {
      expected = new String[]{TOKEN[e.getExpected()]};
    }
    else
    {
      expected = getTokenSet(- e.getState());
    }
    return expected;
  }

  public String getErrorMessage(ParseException e)
  {
    String message = e.getMessage();
    String[] tokenSet = getExpectedTokenSet(e);
    String found = getOffendingToken(e);
    int size = e.getEnd() - e.getBegin();
    message += (found == null ? "" : ", found " + found)
            + "\nwhile expecting "
            + (tokenSet.length == 1 ? tokenSet[0] : java.util.Arrays.toString(tokenSet))
            + "\n"
            + (size == 0 || found != null ? "" : "after successfully scanning " + size + " characters beginning ");
    String prefix = input.subSequence(0, e.getBegin()).toString();
    int line = prefix.replaceAll("[^\n]", "").length() + 1;
    int column = prefix.length() - prefix.lastIndexOf('\n');
    return message
         + "at line " + line + ", column " + column + ":\n..."
         + input.subSequence(e.getBegin(), Math.min(input.length(), e.getBegin() + 64))
         + "...";
  }

  public void parse_size()
  {
    lookahead1(0);                  // double
    consume(1);                     // double
                                                            // line 28 "../../../../../../rex/size.ebnf"
                                                            value = Double.parseDouble(input.subSequence(b0, e0).toString());
                                                            // line 141 "Size.java"
    lookahead1(3);                  // eof | 'B' | 'G' | 'K' | 'M' | 'T'
    switch (l1)
    {
    case 5:                         // 'K'
      {
        consume(5);                 // 'K'
        lookahead1(2);              // eof | 'B'
        if (l1 == 3)                // 'B'
        {
          consume(3);               // 'B'
        }
                                                            // line 31 "../../../../../../rex/size.ebnf"
                                                            value *= 1e3;
                                                            // line 155 "Size.java"
      }
      break;
    case 6:                         // 'M'
      {
        consume(6);                 // 'M'
        lookahead1(2);              // eof | 'B'
        if (l1 == 3)                // 'B'
        {
          consume(3);               // 'B'
        }
                                                            // line 32 "../../../../../../rex/size.ebnf"
                                                            value *= 1e6;
                                                            // line 168 "Size.java"
      }
      break;
    case 4:                         // 'G'
      {
        consume(4);                 // 'G'
        lookahead1(2);              // eof | 'B'
        if (l1 == 3)                // 'B'
        {
          consume(3);               // 'B'
        }
                                                            // line 33 "../../../../../../rex/size.ebnf"
                                                            value *= 1e9;
                                                            // line 181 "Size.java"
      }
      break;
    case 7:                         // 'T'
      {
        consume(7);                 // 'T'
        lookahead1(2);              // eof | 'B'
        if (l1 == 3)                // 'B'
        {
          consume(3);               // 'B'
        }
                                                            // line 34 "../../../../../../rex/size.ebnf"
                                                            value *= 1e12;
                                                            // line 194 "Size.java"
      }
      break;
    default:
      if (l1 == 3)                  // 'B'
      {
        consume(3);                 // 'B'
      }
    }
    lookahead1(1);                  // eof
    consume(2);                     // eof
  }

  private void consume(int t)
  {
    if (l1 == t)
    {
      b0 = b1; e0 = e1; l1 = 0;
    }
    else
    {
      error(b1, e1, 0, l1, t);
    }
  }

  private void lookahead1(int tokenSetId)
  {
    if (l1 == 0)
    {
      l1 = match(tokenSetId);
      b1 = begin;
      e1 = end;
    }
  }

  private int error(int b, int e, int s, int l, int t)
  {
    throw new ParseException(b, e, s, l, t);
  }

  private int     b0, e0;
  private int l1, b1, e1;
  private CharSequence input = null;
  private int size = 0;
  private int begin = 0;
  private int end = 0;

  private int match(int tokenSetId)
  {
    begin = end;
    int current = end;
    int result = INITIAL[tokenSetId];
    int state = 0;

    for (int code = result & 15; code != 0; )
    {
      int charclass;
      int c0 = current < size ? input.charAt(current) : 0;
      ++current;
      if (c0 < 0x80)
      {
        charclass = MAP0[c0];
      }
      else if (c0 < 0xd800)
      {
        int c1 = c0 >> 5;
        charclass = MAP1[(c0 & 31) + MAP1[(c1 & 31) + MAP1[c1 >> 5]]];
      }
      else
      {
        charclass = 0;
      }

      state = code;
      int i0 = (charclass << 4) + code - 1;
      code = TRANSITION[(i0 & 3) + TRANSITION[i0 >> 2]];

      if (code > 15)
      {
        result = code;
        code &= 15;
        end = current;
      }
    }

    result >>= 4;
    if (result == 0)
    {
      end = current - 1;
      int c1 = end < size ? input.charAt(end) : 0;
      if (c1 >= 0xdc00 && c1 < 0xe000)
      {
        --end;
      }
      return error(begin, end, state, -1, -1);
    }

    if (end > size) end = size;
    return (result & 15) - 1;
  }

  private static String[] getTokenSet(int tokenSetId)
  {
    java.util.ArrayList<String> expected = new java.util.ArrayList<>();
    int s = tokenSetId < 0 ? - tokenSetId : INITIAL[tokenSetId] & 15;
    for (int i = 0; i < 8; i += 32)
    {
      int j = i;
      int i0 = (i >> 5) * 11 + s - 1;
      int f = EXPECTED[(i0 & 3) + EXPECTED[i0 >> 2]];
      for ( ; f != 0; f >>>= 1, ++j)
      {
        if ((f & 1) != 0)
        {
          expected.add(TOKEN[j]);
        }
      }
    }
    return expected.toArray(new String[]{});
  }

  private static final int[] MAP0 =
  {
    /*   0 */ 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    /*  35 */ 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 3, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 7,
    /*  70 */ 0, 8, 0, 0, 0, 9, 0, 10, 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 7, 0, 8, 0,
    /* 105 */ 0, 0, 9, 0, 10, 0, 0, 0, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
  };

  private static final int[] MAP1 =
  {
    /*   0 */ 54, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
    /*  26 */ 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
    /*  52 */ 58, 58, 90, 144, 123, 123, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91,
    /*  78 */ 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 91, 12, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    /* 109 */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 7, 0, 8, 0, 0, 0, 9, 0, 10, 0, 0, 0, 0, 0, 0, 11,
    /* 144 */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 2, 3, 0, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0, 0, 0, 0, 0
  };

  private static final int[] INITIAL =
  {
    /* 0 */ 1, 2, 3, 4
  };

  private static final int[] TRANSITION =
  {
    /*   0 */ 53, 53, 53, 53, 53, 53, 52, 53, 58, 53, 52, 53, 53, 75, 53, 53, 63, 68, 72, 53, 84, 89, 72, 53, 80, 53,
    /*  26 */ 53, 53, 53, 93, 78, 53, 54, 53, 53, 53, 59, 53, 53, 53, 64, 53, 53, 53, 85, 53, 53, 53, 96, 53, 53, 53,
    /*  52 */ 11, 0, 0, 0, 0, 80, 5, 0, 0, 0, 96, 38, 0, 0, 0, 112, 38, 0, 39, 42, 43, 42, 43, 0, 8, 8, 0, 9, 0, 0, 64,
    /*  83 */ 64, 39, 0, 0, 0, 128, 39, 0, 39, 42, 0, 9, 9, 0, 48, 48, 48
  };

  private static final int[] EXPECTED =
  {
    /*  0 */ 6, 3, 3, 2, 2, 2, 2, 4, 12, 252
  };

  private static final String[] TOKEN =
  {
    "(0)",
    "double",
    "eof",
    "'B'",
    "'G'",
    "'K'",
    "'M'",
    "'T'"
  };
}

// End
