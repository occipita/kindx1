package kind.x1;

import java.util.Map;
import java.util.HashMap;

/** Modifies a token stream by stripping whitespace and comments, and translates identifierw to keywords as appropriate */
public class TokenStreamMutator implements TokenStream
{
    private final TokenStream source;
    private static Map<String,Integer> keywords;
    
    public TokenStreamMutator (TokenStream source)
    {
        this.source = source;
        
        if (keywords == null) 
        {
            keywords = new HashMap<>();
            for (int i= 0; i <= TokenType.getMax(); i++)
            {
                String name = TokenType.getName(i);
                if (!name.startsWith("KW_")) continue;
                keywords.put (name.substring(3).toLowerCase(), i);
            }
        }
    }
    
    public Token nextToken()
    {
        while (true)
        {
            Token t = source.nextToken();
            
            if (t.type() == TokenType.WS) continue;
            if (t.type() == TokenType.COMMENT) continue;
            if (t.type() == TokenType.ID)
            {
                Integer kw = keywords.get(t.text());
                if (kw != null)
                    t = new Token(kw, t.text());
            }
            return t;
        }
    }
}
