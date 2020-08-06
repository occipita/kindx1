package kind.x1.grammar;

import kind.x1.*;
import static kind.x1.TokenType.*;

public class Misc 
{
    public static ATNBuilder apply (ATNBuilder builder)
    {
        return builder               
                           
            .addLowPriorityProduction("id").s(UNDERSCORE,"id").handler("mergeId").done()
            .addLowPriorityProduction("id").s(ID).handler("copy").done()
            
            .addProduction("sid").beginRepeating().sl("id").repeatWithSeparator(DBLCOLON).handler("sid").done()
            
            .addProduction("id-list").beginRepeating().sl("id").repeatWithSeparator(COMMA).handler("tokenListToStrings").done()
            .addProduction("sid-list").beginRepeating().sl("sid").repeatWithSeparator(COMMA).handler("copy").done()
            
            .addProduction("operator")
                .anyOf(MINUS,DOUBLEPLUS,DOUBLEMINUS,OP_ASSIGN,OP_CONDL,OP_CONDR,OP_CHAIN,OP_LOG_P,OP_LOG_S,OP_COMP,OP_MISC,
                       OP_ARITH_P,OP_ARITH_S,OP_ARITH_I,OP_ARITH_SH,OP_BIT_P,OP_BIT_S,OP_PRE,OP_POST,EQUAL,ASTERISK)
                .asArg(0)
                .handler("copy").done()
            ;
    }

}
