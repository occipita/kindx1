package kind.x1.grammar;

import kind.x1.*;
import static kind.x1.TokenType.*;

public class PatternDefinition 
{
    public static ATNBuilder apply (ATNBuilder builder)
    {
        return applyPattern(builder);
    }  
    public static ATNBuilder applyPattern (ATNBuilder builder)
    {
        return builder
            .addProduction("pattern").s("id").beginOptional().p(COLON).so("typeexpr").endOptional()
                .handler("pattern","named").done()
                
            .addProduction("pattern-list")
                .beginRepeating().sl("pattern").repeatWithSeparator(COMMA)
                .handler("copy").done()
            .addProduction("pattern-list")
                /*empty*/
                .handler("emptyLinkedList").done()
            ;
    }
}
