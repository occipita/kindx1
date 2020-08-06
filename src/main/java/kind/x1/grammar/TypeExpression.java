package kind.x1.grammar;

import kind.x1.*;
import static kind.x1.TokenType.*;

public class TypeExpression 
{
    public static ATNBuilder apply (ATNBuilder builder)
    {
        return applyType(builder);
    }  
    public static ATNBuilder applyType (ATNBuilder builder)
    {
        return builder
            .addProduction("typeexpr").s("typeexpr-term").p(LPAREN).s("type-list").p(RPAREN).handler("type","cons").done()
            .addProduction("typeexpr").s("typeexpr-term").handler("copy").done()
            
            .addProduction("typeexpr-term").s("sid").handler("type","namedType").done()
            
            
            .addProduction("type-list").s("typeexpr").p(COMMA).s("type-list").handler("prepend").done()
            .addProduction("type-list").s("typeexpr").handler("linkedListTail").done()
            
            .addProduction("type-constraint").s("sid").p(LPAREN).s("type-list").p(RPAREN).handler("type","namedConstraint").done()
            ;
    }
}
