package kind.x1.test;

import kind.x1.*;
import kind.x1.grammar.*;
import kind.x1.ast.HandlerRoot;

public class TypeTest extends Assertions implements Runnable
{
    public void run()
    {
        namedType();
        typeConstruction();
        typeConstraints();
    }
    
    public void namedType()
    {
        assertEqual ("namedType",
            testParser("a").parse("typeexpr").toString(),
            "Type.NamedType<a>");
        assertEqual ("namedType (scope specified)",
            testParser("a::b").parse("typeexpr").toString(),
            "Type.NamedType<a::b>");
    }
    public void typeConstruction()
    {
        assertEqual ("typeConstruction",
            testParser("a(b)").parse("typeexpr").toString(),
            "Type.Cons<Type.NamedType<a>,[Type.NamedType<b>]>");
    }
    public void typeConstraints()
    {
        assertEqual ("typeConstraints: named relationship",
            testParser("a(b,c)").parse("type-constraint").toString(),
            "Type.Constraint<a,[Type.NamedType<b>, Type.NamedType<c>]>");
        assertEqual ("typeConstraints: named relationship with scope",
            testParser("scope::a(b,c)").parse("type-constraint").toString(),
            "Type.Constraint<scope::a,[Type.NamedType<b>, Type.NamedType<c>]>");
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
        return Misc.apply(TypeExpression.apply(Expression.apply(new ATNBuilder())))
                .withListener(new HandlerRoot())
                .build();
    }    

}
