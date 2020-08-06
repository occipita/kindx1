package kind.x1.test;

import kind.x1.*;
import kind.x1.grammar.*;
import kind.x1.ast.HandlerRoot;

public class DefnTest  extends Assertions implements Runnable
{
    public void run()
    {
        propDef();
        fnDef();
        operatorDef();
        classDef();
        typeArgs();
        interfaceDef();
        implementationDef();
    }


    public void propDef()
    {
        assertEqual ("propDef: named type",
            testParser("a:b;").parse("defnz").toString(),
            "Defn.Property<a,Type.NamedType<b>,[]>");
        assertEqual ("propDef: named type with initializer",
            testParser("a:int=3;").parse("defnz").toString(),
            "Defn.Property<a,Type.NamedType<int>,["+
                "Defn.PropertyAccessor<INIT,Stmt.Ret<Expr.IntLiteral<3,[]>>>"+
            "]>");
        assertEqual ("propDef: inferred type with initializer",
            testParser("a:=3;").parse("defnz").toString(),
            "Defn.Property<a,inferred,["+
                "Defn.PropertyAccessor<INIT,Stmt.Ret<Expr.IntLiteral<3,[]>>>"+
            "]>");
        assertEqual ("propDef: named type with accessor expr",
            testParser("a:int { get -> 3; }").parse("defnz").toString(),
            "Defn.Property<a,Type.NamedType<int>,["+
                "Defn.PropertyAccessor<GET,Stmt.Ret<Expr.IntLiteral<3,[]>>>"+
            "]>");
        assertEqual ("propDef: inferred type with accessor expr",
            testParser("a { get -> 3; }").parse("defnz").toString(),
            "Defn.Property<a,inferred,["+
                "Defn.PropertyAccessor<GET,Stmt.Ret<Expr.IntLiteral<3,[]>>>"+
            "]>");
        assertEqual ("propDef: named type with accessor block",
            testParser("a:int { get { return 3; } }").parse("defnz").toString(),
            "Defn.Property<a,Type.NamedType<int>,["+
                "Defn.PropertyAccessor<GET,Stmt.Block<[Stmt.Ret<Expr.IntLiteral<3,[]>>]>>"+
            "]>");
        assertEqual ("propDef: all accessor types",
            testParser("a { get {} set {} init {} beforeSet {} afterSet {} beforeGet {} afterInit {} }").parse("defnz").toString(),
            "Defn.Property<a,inferred,["+
                "Defn.PropertyAccessor<GET,Stmt.Block<[]>>, "+
                "Defn.PropertyAccessor<SET,Stmt.Block<[]>>, "+
                "Defn.PropertyAccessor<INIT,Stmt.Block<[]>>, "+
                "Defn.PropertyAccessor<BEFORE_SET,Stmt.Block<[]>>, "+
                "Defn.PropertyAccessor<AFTER_SET,Stmt.Block<[]>>, "+
                "Defn.PropertyAccessor<BEFORE_GET,Stmt.Block<[]>>, "+
                "Defn.PropertyAccessor<AFTER_INIT,Stmt.Block<[]>>"+
            "]>");
    }
    
    public void fnDef()
    {
        assertEqual ("fnDef: named return type",
            testParser("a(x):b { return x; }").parse("defnz").toString(),
            "Defn.Fn<a,Type.NamedType<b>,Defn.FnBody<[Pattern.Named<x,inferred>],Stmt.Block<[Stmt.Ret<Expr.VariableRef<x>>]>>>");
        assertEqual ("fnDef: inferred return type",
            testParser("a(x) { return x; }").parse("defnz").toString(),
            "Defn.Fn<a,inferred,Defn.FnBody<[Pattern.Named<x,inferred>],Stmt.Block<[Stmt.Ret<Expr.VariableRef<x>>]>>>");
        assertEqual ("fnDef: abbreviated",
            testParser("a(x) -> x;").parse("defnz").toString(),
            "Defn.Fn<a,inferred,Defn.FnBody<[Pattern.Named<x,inferred>],Stmt.Ret<Expr.VariableRef<x>>>>");
    }
    public void operatorDef()
    {
        assertEqual ("operatorDef: named return type",
            testParser("operator *** (x):b { return x; }").parse("defnz").toString(),
            "Defn.Op<'***',Type.NamedType<b>,Defn.FnBody<[Pattern.Named<x,inferred>],Stmt.Block<[Stmt.Ret<Expr.VariableRef<x>>]>>>");
        assertEqual ("operatorDef: inferred return type",
            testParser("operator >>= (x) { return x; }").parse("defnz").toString(),
            "Defn.Op<'>>=',inferred,Defn.FnBody<[Pattern.Named<x,inferred>],Stmt.Block<[Stmt.Ret<Expr.VariableRef<x>>]>>>");
        assertEqual ("operatorDef: abbreviated",
            testParser("operator * (x) -> x;").parse("defnz").toString(),
            "Defn.Op<'*',inferred,Defn.FnBody<[Pattern.Named<x,inferred>],Stmt.Ret<Expr.VariableRef<x>>>>");
    }
    public void classDef()
    {
        assertEqual ("classDef: empty",
            testParser("class a {}").parse("defnz").toString(),
            "Defn.ClassDef<a,[],[],default,[],[]>");
        assertEqual ("classDef: with superclasses",
            testParser("class a : b, c {}").parse("defnz").toString(),
            "Defn.ClassDef<a,[],[],default,[Type.NamedType<b>, Type.NamedType<c>],[]>");
        assertEqual ("classDef: with metaclass",
            testParser("class a [b] {}").parse("defnz").toString(),
            "Defn.ClassDef<a,[],[],b,[],[]>");
        assertEqual ("classDef: member function with visibility override",
            testParser("class a [b] { public c(x) { return x; } }").parse("defnz").toString(),
            "Defn.ClassDef<a,[],[],b,[],[Defn.Attr<public,Defn.Fn<c,inferred,Defn.FnBody<[Pattern.Named<x,inferred>],Stmt.Block<[Stmt.Ret<Expr.VariableRef<x>>]>>>>]>");
        assertEqual ("classDef: abstract method",
            testParser("class a [b] { public abstract c(x) : rtype; }").parse("defnz").toString(),
            "Defn.ClassDef<a,[],[],b,[],[Defn.Attr<public abstract,Defn.AbstractFn<c,Type.NamedType<rtype>,[Pattern.Named<x,inferred>]>>]>");
        assertEqual ("classDef: abstract operator",
            testParser("class a [b] { public abstract operator >> (x) : rtype; }").parse("defnz").toString(),
            "Defn.ClassDef<a,[],[],b,[],[Defn.Attr<public abstract,Defn.AbstractOp<'>>',Type.NamedType<rtype>,[Pattern.Named<x,inferred>]>>]>");
        assertEqual ("classDef: public property",
            testParser("class a [b] { public c : d; }").parse("defnz").toString(),
            "Defn.ClassDef<a,[],[],b,[],[Defn.Attr<public,Defn.Property<c,Type.NamedType<d>,[]>>]>");
        assertEqual ("classDef: with type parameters",
            testParser("class a(b,c) {}").parse("defnz").toString(),
            "Defn.ClassDef<a,[b, c],[],default,[],[]>");
    }
        
    public void typeArgs()
    {
        assertEqual ("typeArgs: single",
            testParser("forall t a(x):t {}").parse("defnz").toString(),
            "Defn.ForAll<[t],[],"+
                "Defn.Fn<a,Type.NamedType<t>,Defn.FnBody<[Pattern.Named<x,inferred>],Stmt.Block<[]>>>"+
            ">");
        assertEqual ("typeArgs: multiple with constraints",
            testParser("forall t, u : superclass(t,u) cast(x:t):u {}").parse("defnz").toString(),
            "Defn.ForAll<[t, u],[Type.Constraint<superclass,[Type.NamedType<t>, Type.NamedType<u>]>],"+
                "Defn.Fn<cast,Type.NamedType<u>,Defn.FnBody<[Pattern.Named<x,Type.NamedType<t>>],Stmt.Block<[]>>>"+
            ">");
    }
    
    public void interfaceDef()
    {
        assertEqual ("interfaceDef: empty with no args",
            testParser("interface a {}").parse("defnz").toString(),
            "Defn.InterfaceDef<a,[],[],[],none,[]>");
        assertEqual ("interfaceDef: superinterfaces",
            testParser("interface a : b, c {}").parse("defnz").toString(),
            "Defn.InterfaceDef<a,[],[],[Type.NamedType<b>, Type.NamedType<c>],none,[]>");
        assertEqual ("interfaceDef: type argument",
            testParser("interface a(t) {}").parse("defnz").toString(),
            "Defn.InterfaceDef<a,[t],[],[],none,[]>");
        assertEqual ("interfaceDef: multiple type arguments",
            testParser("interface a(t,u) {}").parse("defnz").toString(),
            "Defn.InterfaceDef<a,[t, u],[],[],none,[]>");
        assertEqual ("interfaceDef: named implementor",
            testParser("interface a => t {}").parse("defnz").toString(),
            "Defn.InterfaceDef<a,[],[],[],[t],[]>");
        assertEqual ("interfaceDef: multi implementor",
            testParser("interface a => t,u {}").parse("defnz").toString(),
            "Defn.InterfaceDef<a,[],[],[],[t, u],[]>");
        assertEqual ("interfaceDef: named implementor missing",
            testParser("interface a => {}").parse("defnz"),
            null);
    
        assertEqual ("interfaceDef: complex test",
            testParser("interface Monad(A) : Functor(A) => M {"+
                       "     static unit(a : A) : M(A);"+
                       //"     forall B bind (f : (A)->M(B)) : M(B);"+ FIXME add this ... and an operator
                       "}").parse("defnz").toString(),
            "Defn.InterfaceDef<Monad,[A],[],[Type.Cons<Type.NamedType<Functor>,[Type.NamedType<A>]>],[M],["+
                "Defn.Attr<static,Defn.AbstractFn<unit,Type.Cons<Type.NamedType<M>,[Type.NamedType<A>]>,[Pattern.Named<a,Type.NamedType<A>>]>>"+
            "]>");
            
        
        // we should support this syntax:
        // interface Monad(A) : Functor(A) => M {
        //    static unit(a : A) : M(A);
        //    forall B operator >>= (f : (A)->M(B)) : M(B);
        // }
        // the brackets on the declaration line are optional. => defines X as a aplaceholder name 
        // used in the declaration. If the interface requires two types to be specified (ie it
        // identifies a rrekationship between types) a comma separated list of ids may be used;
        // if no ids are specified a single type is assumed and cannot be directly referenced.
        // (<list>) adds required type arguments to X (ie declaring
        // that the interface is a constraint on type constructors, not types). (<list> : <constraints>)
        // can also be used
        // example of use:
        // forall A, L, M : Iterable(L), Monad(M)
        // seq (s : L(M(A))) : M(A)
        //    -> s.foldl (\ (l, r) -> l >>= (\ _ -> r) );
    }
    
    public void implementationDef()
    {
        assertEqual ("implementationDef: single type, no args, empty",
            testParser("implementation int => cls {}").parse("defnz").toString(),
            "Defn.ImplementationDef<int,[],[Type.NamedType<cls>],[]>");
        assertEqual ("implementationDef: multiple types, no args, empty",
            testParser("implementation int => cls1, cls2 {}").parse("defnz").toString(),
            "Defn.ImplementationDef<int,[],[Type.NamedType<cls1>, Type.NamedType<cls2>],[]>");
        assertEqual ("implementationDef: single type, args, empty",
            testParser("implementation int(a) => cls {}").parse("defnz").toString(),
            "Defn.ImplementationDef<int,[Type.NamedType<a>],[Type.NamedType<cls>],[]>");
            
        // notes:
        // multi-class interfaces must only contain static members. these are imported into scope whenever the 
        // constraint is used. error occurs if names clash.  would be useful to support renaming/hiding as a constraint option.
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
        return Misc.apply(TypeExpression.apply(Statement.apply(Expression.apply(
                    Definition.apply(PatternDefinition.apply(new ATNBuilder()))))))
                .addProduction("defnz").s("defn").p(TokenType.EOF).handler("copy").done()
                .withListener(new HandlerRoot())
                .build();
    }    

}
