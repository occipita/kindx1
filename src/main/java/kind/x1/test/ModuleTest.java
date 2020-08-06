package kind.x1.test;

import kind.x1.*;
import kind.x1.grammar.*;
import kind.x1.ast.HandlerRoot;

public class ModuleTest  extends Assertions implements Runnable
{
    public void run()
    {
        imports();
        exports();
        definitions();
        wholeFile();
    }
    
    public void imports()
    {
        assertEqual ("imports: single symbol, no rename",
            testParser("import c from a::b;").parse("module-entry").toString(),
            "Mod.Import<a::b,false,[c],default>");
        assertEqual ("imports: multiple symbols, no rename",
            testParser("import c,d from a::b;").parse("module-entry").toString(),
            "Mod.Import<a::b,false,[c, d],default>");
        assertEqual ("imports: single symbol, renamed",
            testParser("import c from a::b as d::e;").parse("module-entry").toString(),
            "Mod.Import<a::b,false,[c],d::e>");            
        assertEqual ("imports: wildcard, no rename",
            testParser("import * from a::b;").parse("module-entry").toString(),
            "Mod.Import<a::b,true,-,default>");            
        assertEqual ("imports: wildcard, renamed",
            testParser("import * from a::b as d::e;").parse("module-entry").toString(),
            "Mod.Import<a::b,true,-,d::e>");            
        
    }
    public void exports()
    {
        assertEqual ("exports",
            testParser("export a,b,c;").parse("module-entry").toString(),
            "Mod.Export<[a, b, c]>");
    }
    public void definitions()
    {
        assertEqual ("definitions: non-exported",
            testParser("a : t;").parse("module-entry").toString(),
            "Mod.Def<Defn.Property<a,Type.NamedType<t>,[]>>");
        assertEqual ("definitions: exported",
            testParser("export a : t;").parse("module-entry").toString(),
            "Mod.ExportDef<Defn.Property<a,Type.NamedType<t>,[]>>");
    }     
    public void wholeFile()
    {
        assertEqual ("wholeFile",
            testParser("import c from a::b;export d : c;").parse("module").toString(),
            "Mod<[Mod.Import<a::b,false,[c],default>, Mod.ExportDef<Defn.Property<d,Type.NamedType<c>,[]>>]>");
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
        return Misc.apply(KModule.apply(TypeExpression.apply(Statement.apply(Expression.apply(
                    Definition.apply(PatternDefinition.apply(new ATNBuilder())))))))
                .withListener(new HandlerRoot())
                .build();
    }    

    
}
