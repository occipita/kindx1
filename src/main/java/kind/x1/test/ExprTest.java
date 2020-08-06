package kind.x1.test;

import kind.x1.*;
import kind.x1.grammar.*;
import kind.x1.ast.HandlerRoot;

public class ExprTest extends Assertions implements Runnable
{
    public void run()
    {
        variableReference();
        literals();
        brackets();
        prec01();
        prec02();
        prec03();
        prec0408();
        prec0913();
        prefix();
        postfix();
        specials();
        lambdas();
    }
    
    private void variableReference()
    {
        assertEqual (
            "variableReference: simple id",
            testParser("id").parse("expr").toString(),
            "Expr.VariableRef<id>");
        assertEqual (
            "variableReference: underscore id",
            testParser("_id").parse("expr").toString(),
            "Expr.VariableRef<_id>");
        assertEqual (
            "variableReference: scoped id",
            testParser("scope::id").parse("expr").toString(),
            "Expr.VariableRef<scope::id>");
    }

    private void literals()
    {
        assertEqual (
            "literals: int parse result",
            testParser("1234").parse("expr").toString(),
            "Expr.IntLiteral<1234,[]>");
        assertEqual (
            "literals: int with flag parse result",
            testParser("1234_flag").parse("expr").toString(),
            "Expr.IntLiteral<1234,[flag]>");
        assertEqual (
            "literals: int with flag list parse result",
            testParser("1234_(flag1,flag2)").parse("expr").toString(),
            "Expr.IntLiteral<1234,[flag1, flag2]>");
        assertEqual (
            "literals: float parse result",
            testParser("123.4").parse("expr").toString(),
            "Expr.FloatLiteral<123.4,[]>");
        assertEqual (
            "literals: float with flag list parse result",
            testParser("123.4_(flag1,flag2)").parse("expr").toString(),
            "Expr.FloatLiteral<123.4,[flag1, flag2]>");
        assertEqual (
            "literals: string parse result",
            testParser("\"hello\"").parse("expr").toString(),
            "Expr.StringLiteral<\"hello\",[]>");
        assertEqual (
            "literals: string with flag list parse result",
            testParser("\"hello\"_(flag1,flag2)").parse("expr").toString(),
            "Expr.StringLiteral<\"hello\",[flag1, flag2]>");
    }
    
    private void brackets()
    {
        assertEqual (
            "brackets: with no other operations",
            testParser("(id)").parse("expr").toString(),
            "Expr.VariableRef<id>");
    }
    private void prec01()
    {
        assertEqual (
            "prec01: simple",
            testParser("a += b").parse("expr").toString(),
            "Expr.RAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>],[+=]>");
        // note '=' isnt OP_ASSIGN so needs extra handling...
        assertEqual (
            "prec01: multiple",
            testParser("a = b = 1").parse("expr").toString(),
            "Expr.RAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.IntLiteral<1,[]>],[=, =]>");
    }
    private void prec02()
    {
        // ?? and : are equivalent, but : is not available i situations where itwould be ambiguous with a declaration.
        // : is mostly provided for similaeity with the ?: operator of other c-like languages
        assertEqual (
            "prec02: simple",
            testParser("a ?? b").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>],[??]>");
        assertEqual (
            "prec02: multiple",
            testParser("a : b : c").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[:, :]>");
    }
    private void prec03()
    {
        assertEqual (
            "prec03: simple",
            testParser("a ? b").parse("expr").toString(),
            "Expr.RAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>],[?]>");
        // note right assoc, not left as described in notes, as this would make the following pointless
        // (as opposed to rarely useful, as itis here):
        assertEqual (
            "prec03: multiple",
            testParser("a ? b ? c").parse("expr").toString(),
            "Expr.RAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[?, ?]>");
        // nb t1 ? a1 : t2 ? a2 : a3 must parse as(t1 ? a1) : ((t2 ? a2) : a3)
        assertEqual (
            "prec03: nested conditions",
            testParser("t1?a1:t2?a2:a3").parse("expr").toString(),
            testParser("(t1?a1):(t2?a2):a3").parse("expr").toString());
    }
    private void prec0408()
    {
        assertEqual (
            "prec0408: 04r",
            testParser("a -> b -> c").parse("expr").toString(),
            "Expr.RAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[->, ->]>");
        assertEqual (
            "prec0408: 04l",
            testParser("a \\ b $> c").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[\\, $>]>");
        assertEqual (
            "prec0408: 05",
            testParser("a || b ^^ c").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[||, ^^]>");
        assertEqual (
            "prec0408: 06",
            testParser("a && b && c").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[&&, &&]>");
        assertEqual (
            "prec0408: 07",
            testParser("a == b <= c").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[==, <=]>");
        assertEqual (
            "prec0408: 08",
            testParser("a $ b \\op\\ c").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[$, \\op\\]>");
        
    }
    private void prec0913()
    {
        assertEqual (
            "prec0913: 09",
            testParser("a + b - c").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[+, -]>");
        assertEqual (
            "prec0913: 10",
            testParser("a * b / c").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[*, /]>");
        assertEqual (
            "prec0913: 11l",
            testParser("a >> b << c").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[>>, <<]>");
        assertEqual (
            "prec0913: 11r",
            testParser("a ** b ** c").parse("expr").toString(),
            "Expr.RAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[**, **]>");
        assertEqual (
            "prec0913: 12",
            testParser("a | b ^ c").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[|, ^]>");
        assertEqual (
            "prec0913: 13",
            testParser("a & b & c").parse("expr").toString(),
            "Expr.LAssoc<[Expr.VariableRef<a>, Expr.VariableRef<b>, Expr.VariableRef<c>],[&, &]>");
    }
    private void prefix ()
    {
        assertEqual ("prefix",
            testParser("- ~ ! ++ --a").parse("expr").toString(),
            "Expr.Apply<Expr.VariableRef<a>,[Expr.Op<--pre>, Expr.Op<++pre>, Expr.Op<!>, Expr.Op<~>, Expr.Op<->]>");
    }
    private void postfix ()
    {
        assertEqual ("postfix",
            testParser("a++ -- \\test!").parse("expr").toString(),
            "Expr.Apply<Expr.VariableRef<a>,[Expr.Op<++post>, Expr.Op<--post>, Expr.Op<\\test!>]>");
    }
    private void specials ()
    {
        assertEqual ("specials: dot",
            testParser("a.b.c").parse("expr").toString(),
            "Expr.Apply<Expr.VariableRef<a>,[Expr.Dot<b>, Expr.Dot<c>]>");
        assertEqual ("specials: index",
            testParser("a[b]").parse("expr").toString(),
            "Expr.Apply<Expr.VariableRef<a>,[Expr.Index<[Expr.VariableRef<b>]>]>");
        assertEqual ("specials: multidimensional index",
            testParser("a[b,c][d]").parse("expr").toString(),
            "Expr.Apply<Expr.VariableRef<a>,[Expr.Index<[Expr.VariableRef<b>, Expr.VariableRef<c>]>, Expr.Index<[Expr.VariableRef<d>]>]>");
        assertEqual ("specials: function call",
            testParser("a()").parse("expr").toString(),
            "Expr.Apply<Expr.VariableRef<a>,[Expr.FnCall<[]>]>");
        assertEqual ("specials: multi-arg function call",
            testParser("a(b,c)").parse("expr").toString(),
            "Expr.Apply<Expr.VariableRef<a>,[Expr.FnCall<[Expr.VariableRef<b>, Expr.VariableRef<c>]>]>");
            
        assertEqual ("specials: postfix ops and specials",
            testParser("a++.b").parse("expr").toString(),
            "Expr.Apply<Expr.VariableRef<a>,[Expr.Op<++post>, Expr.Dot<b>]>");
        
    }
    public void lambdas()
    {
        assertEqual ("lambdas: single argument / simple expression",
            testParser("\\x->x").parse("exprz").toString(),
            "Expr.Lambda<[Defn.FnBody<[Pattern.Named<x,inferred>],Stmt.Ret<Expr.VariableRef<x>>>]>");
        assertEqual ("lambdas: multiple argument / statement block",
            testParser("\\x,y->{ return x+y; }").parse("exprz").toString(),
            "Expr.Lambda<[Defn.FnBody<[Pattern.Named<x,inferred>, Pattern.Named<y,inferred>],Stmt.Block<["+
                "Stmt.Ret<Expr.LAssoc<[Expr.VariableRef<x>, Expr.VariableRef<y>],[+]>>]>>]>");
        assertEqual ("lambdas: multiple bodies",
            testParser("\\{ x->x, x,y->y }").parse("exprz").toString(),
            "Expr.Lambda<["+
                "Defn.FnBody<[Pattern.Named<x,inferred>],Stmt.Ret<Expr.VariableRef<x>>>, "+
                "Defn.FnBody<[Pattern.Named<x,inferred>, Pattern.Named<y,inferred>],Stmt.Ret<Expr.VariableRef<y>>>"+
                "]>");
                
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
        return Statement.apply(Misc.apply(PatternDefinition.apply(Expression.apply(new ATNBuilder()))))
                .addProduction("exprz").s("expr").p(TokenType.EOF).handler("copy").done()
                .withListener(new HandlerRoot())
                .build();
    }    
}
