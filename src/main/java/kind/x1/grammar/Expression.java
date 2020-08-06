package kind.x1.grammar;

import kind.x1.*;
import static kind.x1.TokenType.*;

public class Expression 
{
    public static ATNBuilder apply (ATNBuilder builder)
    {
        return applyTerms(applyPrefixSuffix(applyPrec(builder)));
    }  
    public static ATNBuilder applyPrec (ATNBuilder builder)
    {
        return builder
            .addProduction("expr").s("expr-p01").handler("copy").done()
            
            .addProduction("expr-p01").beginRepeating()
                .sl("expr-p02").repeatWithSeparator(OP_ASSIGN, EQUAL).asListArg(1)
                .handler("expr","rAssoc").done()
            .addLowPriorityProduction("expr-p02").beginRepeating() // must have lower priority than colon as declarator
                .sl("expr-p03").repeatWithSeparator(OP_CONDR, COLON).asListArg(1) 
                .handler("expr","lAssoc").done()
            .addProduction("expr-p03").beginRepeating()
                .sl("expr-p04l").repeatWithSeparator(OP_CONDL).asListArg(1)
                .handler("expr","rAssoc").done()
            .addProduction("expr-p04l").beginRepeating()
                .sl("expr-p04r").repeatWithSeparator(BACKSLASH,OP_CHAIN,ARROWL).asListArg(1)
                .handler("expr","lAssoc").done()
            .addProduction("expr-p04r").beginRepeating()
                .sl("expr-p05").repeatWithSeparator(ARROWR).asListArg(1)
                .handler("expr","rAssoc").done()
            .addProduction("expr-p05").beginRepeating()
                .sl("expr-p06").repeatWithSeparator(OP_LOG_S).asListArg(1)
                .handler("expr","lAssoc").done()
            .addProduction("expr-p06").beginRepeating()
                .sl("expr-p07").repeatWithSeparator(OP_LOG_P).asListArg(1)
                .handler("expr","lAssoc").done()
            .addProduction("expr-p07").beginRepeating()
                .sl("expr-p08").repeatWithSeparator(OP_COMP).asListArg(1)
                .handler("expr","lAssoc").done()
            .addProduction("expr-p08").beginRepeating()
                .sl("expr-p09").repeatWithSeparator(OP_MISC).asListArg(1)
                .handler("expr","lAssoc").done()
            .addProduction("expr-p09").beginRepeating()
                .sl("expr-p10").repeatWithSeparator(OP_ARITH_S,MINUS).asListArg(1)
                .handler("expr","lAssoc").done()
            .addProduction("expr-p10").beginRepeating()
                .sl("expr-p11l").repeatWithSeparator(OP_ARITH_P,ASTERISK).asListArg(1)
                .handler("expr","lAssoc").done()
            .addProduction("expr-p11l").beginRepeating()
                .sl("expr-p11r").repeatWithSeparator(OP_ARITH_SH).asListArg(1)
                .handler("expr","lAssoc").done()
            .addProduction("expr-p11r").beginRepeating()
                .sl("expr-p12").repeatWithSeparator(OP_ARITH_I).asListArg(1)
                .handler("expr","rAssoc").done()
            .addProduction("expr-p12").beginRepeating()
                .sl("expr-p13").repeatWithSeparator(OP_BIT_S).asListArg(1)
                .handler("expr","lAssoc").done()
            .addProduction("expr-p13").beginRepeating()
                .sl("expr-special").repeatWithSeparator(OP_BIT_P).asListArg(1)
                .handler("expr","lAssoc").done();
    }
    public static ATNBuilder applyPrefixSuffix (ATNBuilder builder)
    {
        return builder               
                
            .addProduction("expr-special").s("expr-prefix-list","expr-term","expr-suffix-list").handler("expr","apply").done()
            
            .addProduction("expr-suffix-list").p(DOT).s("id","expr-suffix-list").handler("expr","dot").done()
            .addProduction("expr-suffix-list").p(LSQBR)
                .beginRepeating().sl("expr").repeatWithSeparator(COMMA).p(RSQBR).s("expr-suffix-list")
                .handler("expr","index").done()
            .addProduction("expr-suffix-list").p(LPAREN)
                .beginRepeating().sl("expr").repeatWithSeparator(COMMA).p(RPAREN).s("expr-suffix-list")
                .handler("expr","fnCall").done()
            .addProduction("expr-suffix-list").p(LPAREN,RPAREN).s("expr-suffix-list").handler("expr","emptyFnCall").done()
            .addProduction("expr-suffix-list").s(OP_POST).s("expr-suffix-list").handler("expr","postOp").done()
            .addProduction("expr-suffix-list").s(DOUBLEPLUS).s("expr-suffix-list").handler("expr","postOp").done()
            .addProduction("expr-suffix-list").s(DOUBLEMINUS).s("expr-suffix-list").handler("expr","postOp").done()
            .addProduction("expr-suffix-list") /* empty */ .handler("emptyLinkedList").done()
            
            .addProduction("expr-prefix-list").s(OP_PRE).s("expr-prefix-list").handler("expr","preOp").done()
            .addProduction("expr-prefix-list").s(MINUS).s("expr-prefix-list").handler("expr","preOp").done()
            .addProduction("expr-prefix-list").s(DOUBLEPLUS).s("expr-prefix-list").handler("expr","preOp").done()
            .addProduction("expr-prefix-list").s(DOUBLEMINUS).s("expr-prefix-list").handler("expr","preOp").done()
            .addProduction("expr-prefix-list") /* empty */ .handler("emptyLinkedList").done();
            
            
    }
    public static ATNBuilder applyTerms (ATNBuilder builder)
    {
        return builder               
            .addProduction("expr-term").s("sid").handler("expr","variableRef").done()
            .addProduction("expr-term").s(INTLITERAL).p(UNDERSCORE).s("expr-flag-list").handler("expr","intLiteral").done()
            .addProduction("expr-term").s(INTLITERAL).handler("expr","intLiteral").done()
            .addProduction("expr-term").s(FLOATLITERAL).p(UNDERSCORE).s("expr-flag-list").handler("expr","floatLiteral").done()
            .addProduction("expr-term").s(FLOATLITERAL).handler("expr","floatLiteral").done()
            .addProduction("expr-term").s(STRINGLITERAL).p(UNDERSCORE).s("expr-flag-list").handler("expr","stringLiteral").done()
            .addProduction("expr-term").s(STRINGLITERAL).handler("expr","stringLiteral").done()   
            .addProduction("expr-term").p(LPAREN).s("expr").p(RPAREN).handler("copy").done()
            .addProduction("expr-term").p(BACKSLASH,LBRACE).beginRepeating().sl("lambda-body").repeatWithSeparator(COMMA).p(RBRACE)
                .handler("expr","lambda").done()
            .addProduction("expr-term").p(BACKSLASH).sl("lambda-body").handler("expr","lambda").done()
            .addProduction("expr-flag-list").sl(ID).handler("expr","flagList").done()
            .addProduction("expr-flag-list").p(LPAREN).beginRepeating().sl("id").repeatWithSeparator(COMMA).p(RPAREN)
                .handler("expr","flagList").done()
            .addProduction("lambda-body").s("pattern-list").p(ARROWR).s("expr").handler("defn","fnBody").done()
            .addProduction("lambda-body").s("pattern-list").p(ARROWR).s("stmt-block").handler("defn","fnBody").done()
            ;
    }
}
