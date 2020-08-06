package kind.x1.test;

import kind.x1.*;
import kind.x1.grammar.*;
import kind.x1.ast.HandlerRoot;

public class PatternTest  extends Assertions implements Runnable
{
    public void run()
    {
        namedObject();
        patternList();
    }

    public void namedObject()
    {
        assertEqual("namedObject: no type",
            testParser("x").parse("pattern").toString(),
            "Pattern.Named<x,inferred>");
        assertEqual("namedObject: specified type",
            testParser("x:T").parse("pattern").toString(),
            "Pattern.Named<x,Type.NamedType<T>>");            
    }
    public void patternList()
    {
        assertEqual("patternList: one element",
            testParser("x").parse("pattern-list").toString(),
            "[Pattern.Named<x,inferred>]");
        assertEqual("patternList: two elements",
            testParser("x,y").parse("pattern-list").toString(),
            "[Pattern.Named<x,inferred>, Pattern.Named<y,inferred>]");
        assertEqual("patternList: no elements",
            testParser("").parse("pattern-list").toString(),
            "[]");
    }
    private ALLStar testParser (String content)
    {
        ALLStar result =new ALLStar(atn);
        Lexer lex = new Lexer();
        lex.setInput(content);
        TokenStreamMutator tokens = new TokenStreamMutator(lex);
        result.setInput(tokens);
        return result;
    }
    
    private static final ATN atn = atn();
    
    private static ATN atn()
    {
        return Misc.apply(TypeExpression.apply(PatternDefinition.apply(new ATNBuilder())))
                .withListener(new HandlerRoot())
                .build();
    }    

}
