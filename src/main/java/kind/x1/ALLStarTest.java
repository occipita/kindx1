package kind.x1;

import java.util.*;
import static kind.x1.TokenType.*;

public class ALLStarTest extends Assertions implements Runnable
{
    public void run ()
    {
        llPredictUnambiguous ();
        llPredictSimple ();
        llPredictAmbiguous();
        parseHandlersInvoked();
        buildsPossibleTypeList();
        cartesianProduct();
        parseNonterminal();
        parseRepeating();
        parseOptional();
    }
    
    public void llPredictUnambiguous()
    {
        ALLStar t = testParser(simpleATN(), "id1 id2");
        assertEqual ("llPredictUnambiguous: predicted production", t.llPredict("start", GSS.empty()), 1);
    }
    public void llPredictSimple()
    {
        ALLStar t = testParser(atnWithSubDef(), "id1 id2 \"string\"");
        assertEqual ("llPredictSimple: predicted production", t.llPredict("sub", GSS.empty()), 3);
    }
    public void llPredictAmbiguous()
    {
        ALLStar t = testParser(ambiguousATN(), "id1 id2"); // start of input could match production 1 or 2, so should prefer 1
        //t.setDebug(true);
        assertEqual ("llPredictAmbiguous: predicted production", t.llPredict("start", GSS.empty()), 1);
    }
    public void parseHandlersInvoked ()
    {
        assertEqual ("parseHandlersInvoked: parse result", testParser(simpleATN(), "id1 id2").parse("start"), "<ID(id1),ID(id2)>");
    }
    public void buildsPossibleTypeList()
    {
        //FIXMe should be in a util class
        assertEqual("buildsPossibleTypeList: types for Token", 
            ALLStar.possibleTypeList(Token.class), 
            Arrays.asList(Token.class, Object.class));
    }
    public void cartesianProduct ()
    {
        // FIXME should be in a util class
        assertEqual("cartesianProduct: of [1,2], [3,4,5], [6,7]",
            ALLStar.cartesianProduct(Arrays.asList(1,2), Arrays.asList(3,4,5), Arrays.asList(6,7)),
            Arrays.asList(
                Arrays.asList(1,3,6), Arrays.asList(1,3,7),
                Arrays.asList(1,4,6), Arrays.asList(1,4,7),
                Arrays.asList(1,5,6), Arrays.asList(1,5,7),

                Arrays.asList(2,3,6), Arrays.asList(2,3,7),
                Arrays.asList(2,4,6), Arrays.asList(2,4,7),
                Arrays.asList(2,5,6), Arrays.asList(2,5,7)));
    }
    public void parseNonterminal ()
    {
        assertEqual ("parseNonterminal: parse result", 
            testParser(atnWithSubDef(), "id1 42 id2;").parse("start"), 
            "<ID(id1),<INTLITERAL(42),ID(id2)>>");
    }
    public void parseRepeating()
    {
        assertEqual("parseRepeating: parse result",
            testParser(new ATNBuilder()
                .addProduction("start").beginRepeating().token(ID).asListArg(0).repeatWithSeparator(COMMA).token(SEMICOLON)
                    .handler("buildSingleton").done()
                .withListener(new DefaultListener()).build(), "id1, id2, id3;").parse("start"),
            "<[ID(id1), ID(id2), ID(id3)]>");
    }
    public void parseOptional()
    {
        assertEqual("parseOptional: parse result",
            testParser(new ATNBuilder()
                .addProduction("start").nonterminal("sub").asArg(0).nonterminal("sub").asArg(1)
                    .handler("buildPair").done()
                .addProduction("sub").beginOptional().token(ID).asOptionalArg(0).endOptional().token(SEMICOLON)
                    .handler("optionalSingleton").done()
                .withListener(new DefaultListener()).build(), "id; ;").parse("start"),
            "<<ID(id)>,empty>");
    }
                
    private ALLStar testParser (ATN atn, String content)
    {
        ALLStar result =new ALLStar(atn);
        Lexer lex = new Lexer();
        lex.setInput(content);
        TokenStreamMutator tokens = new TokenStreamMutator(lex);
        result.setInput(tokens);
        return result;
    }
    
    class DefaultListener
    {
        public String buildSingleton (Object a) { return "<"+a+">"; }
        public String buildPair (Object a, Object b) { return "<"+a+","+b+">"; }
        public String buildTriple (Object a, Object b, Object c) { return "<"+a+","+b+","+c+">"; }
        public String optionalSingleton(Optional<?> a) { return a.isPresent() ? buildSingleton(a.get()) : "empty"; }
    }
    private ATN simpleATN ()
    {
        return new ATNBuilder()
            .addProduction("start").token(ID).asArg(0).token(ID).asArg(1).handler("buildPair").done()
            .withListener(new DefaultListener())
            .build();
    }
    private ATN atnWithSubDef()
    {
        return new ATNBuilder()
            .addProduction("start").token(ID).asArg(0).nonterminal("sub").asArg(1).token(SEMICOLON).handler("buildPair").done()
            .addProduction("sub").token(INTLITERAL).asArg(0).token(ID).asArg(1).handler("buildPair").done()
            .addProduction("sub").token(ID).asArg(0).token(STRINGLITERAL).asArg(1).handler("buildPair").done()
            .withListener(new DefaultListener())
            .build();
    }
    private ATN simpleExprATN ()
    {
        return new ATNBuilder()
            .addProduction("expr").token(INTLITERAL).token(OP_ARITH_P).nonterminal("expr").handler("buildTriple").done()
            .addProduction("expr").token(INTLITERAL).token(OP_ARITH_S).nonterminal("expr").handler("buildTriple").done()
            .addProduction("expr").token(INTLITERAL).handler("buildSingleton").done()
            .withListener(new DefaultListener())
            .build();
    }
    private ATN ambiguousATN ()
    {
        return new ATNBuilder()
            .addProduction("start").token(ID).nonterminal("a").token(EOF).handler("buildPair").done()
            .addProduction("start").nonterminal("a").token(ID).token(EOF).handler("buildPair").done()
            .addProduction("a").token(INTLITERAL).handler("buildSingleton").done()
            .addProduction("a").token(ID).handler("buildSingleton").done()
            .withListener(new DefaultListener())
            .build(); // has 2 valid parses for ID ID, but should prefer <ID,<ID>> over <<ID>,ID>
    }
}
