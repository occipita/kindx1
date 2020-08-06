package kind.x1;

import static kind.x1.TokenType.*;

public class LexerTest 
{
    public void run()
    {
        test ("EOF", "", EOF);
        test ("WhitespaceAndComments",
               " /* comment */     // comment\n//comment2\n\n   /*\n*/ \t\r\n",
               WS, COMMENT, WS, COMMENT, COMMENT, WS, COMMENT, WS, EOF);
        test ("Hexliteral", "0x1234 0xabAB 0x12_34 0x12_", 
               HEXLITERAL, WS, HEXLITERAL, WS, HEXLITERAL, WS, HEXLITERAL, UNDERSCORE, EOF);
        test ("Binliteral", "0b01101 0B1111 0b0 0b01_1111 0b0_",
               BINLITERAL, WS, BINLITERAL, WS, BINLITERAL, WS, BINLITERAL, WS, BINLITERAL, UNDERSCORE, EOF);
        test ("Octliteral", "0o1234567 0O1234 0o0 0o01_234 0o0_",
               OCTLITERAL, WS, OCTLITERAL, WS, OCTLITERAL, WS, OCTLITERAL, WS, OCTLITERAL, UNDERSCORE, EOF);
        test ("Intliteral", "5 320 01234 45_678 1_",
               INTLITERAL, WS, INTLITERAL, WS, INTLITERAL, WS, INTLITERAL, WS, INTLITERAL, UNDERSCORE, EOF);
        test ("Intliteral@eof", "8888", INTLITERAL, EOF);
        test ("NegativeIntliteral", "-5 -320 -01234 -45_678 -1_",
               INTLITERAL, WS, INTLITERAL, WS, INTLITERAL, WS, INTLITERAL, WS, INTLITERAL, UNDERSCORE, EOF);
        test ("FloatLiteral(basic)", "1.234567890", FLOATLITERAL, EOF); 
        test ("FloatLiteral(frac sep)", "0.123_456", FLOATLITERAL, EOF);
        test ("FloatLiteral(whole sep)", "123_456.0", FLOATLITERAL, EOF);
        test ("FloatLiteral(neg)", "-1.2", FLOATLITERAL, EOF);
        test ("FloatLiteral(exp)", "1e2", FLOATLITERAL, EOF);
        test ("FloatLiteral(frac+exp)", "1.1e3", FLOATLITERAL, EOF);
        test ("FloatLiteral(neg exp)", "1e-1", FLOATLITERAL, EOF);
        test ("FloatLiteral(pos exp)", "2e+2", FLOATLITERAL, EOF);
        test ("FloatLiteral(exp sep)", "1e1_000", FLOATLITERAL, EOF);
        test ("FloatLiteral then underscore", "1.23_", FLOATLITERAL, UNDERSCORE, EOF);
        test ("BaseLiteral neg", "-0x0 -0o0 -0b0", HEXLITERAL, WS, OCTLITERAL, WS, BINLITERAL, EOF);
        test ("StringLiteral(basic)", "\"string literal\"", STRINGLITERAL, EOF);
        test ("StringLiteral(escape)", "\"string \\\"escape\\\" \\x23\\u3456 \\\\\"", STRINGLITERAL, EOF);
        test ("Identifiers", "i id id123 i_id id_", ID, WS, ID, WS, ID, WS, ID, WS, ID, EOF);
        // nb ID doesnt start with underscore ... these are merged during parse except when uscore is a separator
        test ("Underscore id", "_id", UNDERSCORE, ID, EOF);
        test ("SingleChars", "_-(){}[];:,.\\=", UNDERSCORE, MINUS, LPAREN, RPAREN, LBRACE, RBRACE, LSQBR, RSQBR, SEMICOLON, COLON, COMMA, DOT, BACKSLASH, EQUAL, EOF);
        test ("Operators 1", "++ -- + - * / % ^ | & || ^^ &&", DOUBLEPLUS, WS, DOUBLEMINUS, 
                WS, OP_ARITH_S, WS, MINUS, WS, ASTERISK, WS, OP_ARITH_P, WS, OP_ARITH_P, 
                WS, OP_BIT_S, WS, OP_BIT_S, WS, OP_BIT_P, WS, OP_LOG_S, WS, OP_LOG_S, WS, OP_LOG_P);
        test ("Operators 2", "? ?? -> => <- \\> ** << >>", OP_CONDL, WS, OP_CONDR, WS, ARROWR, WS, ARROWRD, WS, ARROWL, WS, OP_CHAIN, WS, 
                OP_ARITH_I, WS, OP_ARITH_SH, WS, OP_ARITH_SH, EOF); 
        test ("Operators 3", "< > <= >= == != ~ !", OP_COMP, WS, OP_COMP, WS, OP_COMP, WS, OP_COMP, WS, OP_COMP, WS, OP_COMP, WS, OP_PRE, WS, OP_PRE, EOF);
        test ("Custom operators 1", "#= ++= */=", OP_ASSIGN, WS, OP_ASSIGN, WS, OP_ASSIGN, EOF);
        test ("Custom operators 2", "<<< >>> <*< <*>", OP_CHAIN, WS, OP_CHAIN, WS, OP_CHAIN, WS, OP_CHAIN, EOF);
        test ("Custom operators 3", "# ## $+ >*", OP_MISC, WS, OP_MISC, WS, OP_MISC, WS, OP_MISC, EOF);
        test ("Custom operators 4", "+$ -$ /$ *$", OP_ARITH_S, WS, OP_ARITH_S, WS, OP_ARITH_P, WS, OP_ARITH_P, EOF);
        test ("Custom operators 5", "\\test\\ \\test: \\test!", OP_MISC, WS, OP_PRE, WS, OP_POST, EOF);
        test ("DoubleColon", "id1::id2", ID, DBLCOLON, ID);
    }
    
    private void test (String name, String content, int... tokens)
    {
        try
        {
            Lexer lex = new Lexer();
            lex.setInput(content);
            for (int t = 0; t < tokens.length; t++)
            {
                if (tokens[t] == -2) 
                {
                    lex.setDebug(true);
                    continue;
                }
                Token tok = lex.nextToken();
                if (tok.type() != tokens[t])
                {
                    System.err.println ("LexerTest."+name+": after " + t + " tokens expected "+getName(tokens[t])+" but got "+tok);
                    return;
                }
            }   
        }
        catch (Exception e)
        {
            System.err.println ("LexerTest."+name+": failed due to exception");
            e.printStackTrace();
        }
    }
}