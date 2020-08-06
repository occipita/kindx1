package kind.x1.test;

import kind.x1.*;
import kind.x1.grammar.*;
import kind.x1.ast.HandlerRoot;

public class StmtTest  extends Assertions implements Runnable
{
    public void run()
    {
        expression();
        returns();
        block();
        nullStmt();
        varDecl();
        whileStmt();
        ifStmt();
    }
    
    public void expression()
    {
        assertEqual ("expression",
            testParser("a;").parse("stmt").toString(),
            "Stmt.Exp<Expr.VariableRef<a>>");
    }
    public void returns()
    {
        assertEqual ("returns: empty",
            testParser("return;").parse("stmt").toString(),
            "Stmt.Ret<void>");
        assertEqual ("returns: with expression",
            testParser("return a;").parse("stmt").toString(),
            "Stmt.Ret<Expr.VariableRef<a>>");
    }
    public void block()
    {
        assertEqual ("block",
            testParser("{}").parse("stmt").toString(),
            "Stmt.Block<[]>");
        assertEqual ("block",
            testParser("{a;b;}").parse("stmt").toString(),
            "Stmt.Block<[Stmt.Exp<Expr.VariableRef<a>>, Stmt.Exp<Expr.VariableRef<b>>]>");
        assertEqual ("block",
            testParser("{a;{b;}}").parse("stmt").toString(),
            "Stmt.Block<[Stmt.Exp<Expr.VariableRef<a>>, Stmt.Block<[Stmt.Exp<Expr.VariableRef<b>>]>]>");
    }
    public void nullStmt()
    {
        assertEqual ("nullStmt",
            testParser(";").parse("stmt").toString(),
            "Stmt.Null");
    }
    public void varDecl()
    {
        assertEqual ("varDecl",
            testParser("a := b;").parse("stmt").toString(),
            "Stmt.VarDecl<[a],inferred,Expr.VariableRef<b>>");
        assertEqual ("varDecl",
            testParser("a,b : = c;").parse("stmt").toString(),
            "Stmt.VarDecl<[a, b],inferred,Expr.VariableRef<c>>");
        assertEqual ("varDecl",
            testParser("a,b : Type = c;").parse("stmt").toString(),
            "Stmt.VarDecl<[a, b],Type.NamedType<Type>,Expr.VariableRef<c>>");
        assertEqual ("varDecl",
            testParser("a,b : Type;").parse("stmt").toString(),
            "Stmt.VarDecl<[a, b],Type.NamedType<Type>,default>");
    }
    
    public void whileStmt()
    {
        assertEqual ("whileStmt",
            testParser("while(a) b;").parse("stmt").toString(),
            "Stmt.While<Expr.VariableRef<a>,Stmt.Exp<Expr.VariableRef<b>>>");
    }
    public void ifStmt()
    {
        assertEqual ("ifStmt - no else",
            testParser("if (a) b;").parse("stmt").toString(),
            "Stmt.If<Expr.VariableRef<a>,Stmt.Exp<Expr.VariableRef<b>>,none>");
        assertEqual ("ifStmt - simple else",
            testParser("if (a) b; else c;").parse("stmt").toString(),
            "Stmt.If<Expr.VariableRef<a>,Stmt.Exp<Expr.VariableRef<b>>,Stmt.Exp<Expr.VariableRef<c>>>");
        assertEqual ("ifStmt - nested else should attach to innermost if",
            testParser("if (a) if (d) b; else c;").parse("stmt").toString(),
            "Stmt.If<Expr.VariableRef<a>,Stmt.If<Expr.VariableRef<d>,Stmt.Exp<Expr.VariableRef<b>>,Stmt.Exp<Expr.VariableRef<c>>>,none>");
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
        return Misc.apply(Statement.apply(TypeExpression.apply(Expression.apply(new ATNBuilder()))))
                .withListener(new HandlerRoot())
                .build();
    }    

}
