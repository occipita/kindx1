package kind.x1;

import kind.x1.ast.HandlerRoot;
import kind.x1.ast.Mod;
import kind.x1.grammar.*;
import java.io.*;

public class KindParser 
{
    private static final ATN atn = atn();
    private final ALLStar parser = new ALLStar(atn);
    
    public Mod parse (File source) throws IOException
    {
        Lexer lex = new Lexer();
        try (BufferedReader in = new BufferedReader(new FileReader(source)))
        {
            lex.setInput(in);
            TokenStreamMutator tokens = new TokenStreamMutator(lex);
            parser.setInput(tokens);
            return (Mod)parser.parse("module");
        }
    }            
    
    private static ATN atn()
    {
        return Misc.apply(KModule.apply(TypeExpression.apply(Statement.apply(Expression.apply(
                    Definition.apply(PatternDefinition.apply(new ATNBuilder())))))))
                .withListener(new HandlerRoot())
                .build();
    }    

}
