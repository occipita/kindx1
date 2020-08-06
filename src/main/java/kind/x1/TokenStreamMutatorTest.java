package kind.x1;

import static kind.x1.TokenType.*;

public class TokenStreamMutatorTest
{
    public void run()
    {
        test ("StripsWS", "hello world", ID, ID, EOF);
        test ("StripsComments", "hello/* COMMENT */world", ID, ID, EOF);
        test ("ConvertsKeywords", "if class", KW_IF, KW_CLASS, EOF);
    }
    
    private void test (String name, String content, int... tokens)
    {
        try
        {
            Lexer lex = new Lexer();
            lex.setInput(content);
            TokenStreamMutator sut = new TokenStreamMutator(lex);
            for (int t = 0; t < tokens.length; t++)
            {
                if (tokens[t] == -2) 
                {
                    continue;
                }
                Token tok = sut.nextToken();
                if (tok.type() != tokens[t])
                {
                    System.err.println ("TokenStreamMutatorTest."+name+": after " + t + " tokens expected "+getName(tokens[t])+" but got "+tok);
                    return;
                }
            }   
        }
        catch (Exception e)
        {
            System.err.println ("TokenStreamMutatorTest."+name+": failed due to exception");
            e.printStackTrace();
        }
    }
}