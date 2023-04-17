// This file was generated on Sat Apr 15, 2023 11:21 (UTC+02) by REx v5.57 which is Copyright (c) 1979-2023 by Gunther Rademacher <grd@gmx.net>
// REx command line: size.ebnf -java -a java -name de.bottlecaps.rex.benchmark.json.Size

                                                            // line 2 "size.ebnf"
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
                                                            // line 28 "size.ebnf"
                                                            value = Double.parseDouble(input.subSequence(b0, e0).toString());
                                                            // line 141 "Size.java"
    lookahead1(4);                  // ws | eof | 'B' | 'E' | 'G' | 'K' | 'M' | 'P' | 'T'
    if (l1 == 2)                    // ws
    {
      consume(2);                   // ws
    }
    lookahead1(3);                  // eof | 'B' | 'E' | 'G' | 'K' | 'M' | 'P' | 'T'
    switch (l1)
    {
    case 7:                         // 'K'
      {
        consume(7);                 // 'K'
        lookahead1(2);              // eof | 'B'
        if (l1 == 4)                // 'B'
        {
          consume(4);               // 'B'
        }
                                                            // line 32 "size.ebnf"
                                                            value *= 1024.0;
                                                            // line 160 "Size.java"
      }
      break;
    case 8:                         // 'M'
      {
        consume(8);                 // 'M'
        lookahead1(2);              // eof | 'B'
        if (l1 == 4)                // 'B'
        {
          consume(4);               // 'B'
        }
                                                            // line 33 "size.ebnf"
                                                            value *= 1024.0 * 1024.0;
                                                            // line 173 "Size.java"
      }
      break;
    case 6:                         // 'G'
      {
        consume(6);                 // 'G'
        lookahead1(2);              // eof | 'B'
        if (l1 == 4)                // 'B'
        {
          consume(4);               // 'B'
        }
                                                            // line 34 "size.ebnf"
                                                            value *= 1024.0 * 1024.0 * 1024.0;
                                                            // line 186 "Size.java"
      }
      break;
    case 10:                        // 'T'
      {
        consume(10);                // 'T'
        lookahead1(2);              // eof | 'B'
        if (l1 == 4)                // 'B'
        {
          consume(4);               // 'B'
        }
                                                            // line 35 "size.ebnf"
                                                            value *= 1024.0 * 1024.0 * 1024.0 * 1024.0;
                                                            // line 199 "Size.java"
      }
      break;
    case 9:                         // 'P'
      {
        consume(9);                 // 'P'
        lookahead1(2);              // eof | 'B'
        if (l1 == 4)                // 'B'
        {
          consume(4);               // 'B'
        }
                                                            // line 36 "size.ebnf"
                                                            value *= 1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0;
                                                            // line 212 "Size.java"
      }
      break;
    case 5:                         // 'E'
      {
        consume(5);                 // 'E'
        lookahead1(2);              // eof | 'B'
        if (l1 == 4)                // 'B'
        {
          consume(4);               // 'B'
        }
                                                            // line 37 "size.ebnf"
                                                            value *= 1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0;
                                                            // line 225 "Size.java"
      }
      break;
    default:
      if (l1 == 4)                  // 'B'
      {
        consume(4);                 // 'B'
      }
    }
    lookahead1(1);                  // eof
    consume(3);                     // eof
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
      code = TRANSITION[(i0 & 7) + TRANSITION[i0 >> 3]];

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
    for (int i = 0; i < 11; i += 32)
    {
      int j = i;
      int i0 = (i >> 5) * 13 + s - 1;
      int f = EXPECTED[i0];
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
    /*   0 */ 14, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0,
    /*  35 */ 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 3, 4, 0, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 8,
    /*  70 */ 0, 9, 0, 0, 0, 10, 0, 11, 0, 0, 12, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 8, 0, 9,
    /* 104 */ 0, 0, 0, 10, 0, 11, 0, 0, 12, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
  };

  private static final int[] MAP1 =
  {
    /*   0 */ 54, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
    /*  26 */ 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
    /*  52 */ 58, 58, 90, 132, 162, 162, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100,
    /*  74 */ 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 14, 0, 0, 0, 0, 0, 0, 0,
    /*  98 */ 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,
    /* 133 */ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 3, 4, 0, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 0, 0, 0, 7, 0, 0, 8,
    /* 168 */ 0, 9, 0, 0, 0, 10, 0, 11, 0, 0, 12, 0, 0, 0, 13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
  };

  private static final int[] INITIAL =
  {
    /* 0 */ 1, 2, 3, 4, 5
  };

  private static final int[] TRANSITION =
  {
    /*   0 */ 121, 121, 116, 120, 121, 112, 30, 112, 32, 121, 40, 55, 47, 55, 73, 121, 65, 60, 78, 121, 83, 121, 88,
    /*  23 */ 121, 93, 121, 98, 121, 106, 121, 6, 0, 0, 0, 0, 0, 0, 0, 10, 10, 39, 0, 0, 0, 0, 39, 0, 40, 0, 0, 0, 0,
    /*  52 */ 40, 0, 40, 0, 44, 45, 44, 45, 0, 0, 0, 11, 0, 0, 0, 0, 96, 96, 0, 11, 11, 0, 0, 80, 80, 80, 0, 0, 0, 112,
    /*  82 */ 112, 0, 0, 0, 128, 128, 0, 0, 0, 144, 144, 0, 0, 0, 160, 160, 0, 0, 0, 176, 176, 0, 0, 0, 0, 64, 64, 64,
    /* 110 */ 64, 0, 0, 0, 13, 0, 0, 0, 0, 0, 57, 0, 0, 0, 0, 0, 0, 0, 0
  };

  private static final int[] EXPECTED =
  {
    /*  0 */ 2, 8, 24, 2040, 2044, 2, 2, 2, 4, 2, 2, 2, 2
  };

  private static final String[] TOKEN =
  {
    "(0)",
    "double",
    "ws",
    "eof",
    "'B'",
    "'E'",
    "'G'",
    "'K'",
    "'M'",
    "'P'",
    "'T'"
  };
}

// End
