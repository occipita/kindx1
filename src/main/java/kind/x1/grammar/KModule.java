package kind.x1.grammar;

import kind.x1.*;
import static kind.x1.TokenType.*;

public class KModule 
{
    public static ATNBuilder apply (ATNBuilder builder)
    {
        return builder               
            .addProduction("module-entry")
                .p(KW_IMPORT).s("id-list").p(KW_FROM).s("sid")
                .beginOptional().p(KW_AS).so("sid").endOptional()
                .p(SEMICOLON)
                .handler("mod","importSpecified").done()
            .addProduction("module-entry")
                .p(KW_IMPORT,ASTERISK).p(KW_FROM).s("sid")
                .beginOptional().p(KW_AS).so("sid").endOptional()
                .p(SEMICOLON)
                .handler("mod","importWild").done()
            .addProduction("module-entry")
                .p(KW_EXPORT).s("id-list").p(SEMICOLON)
                .handler("mod","export").done()
            .addProduction("module-entry").s("defn").handler("mod","def").done()
            .addProduction("module-entry").p(KW_EXPORT).s("defn").handler("mod","exportDef").done()
            
            .addProduction("module")
                .beginRepeating().sl("module-entry").repeatUntilTerminator(EOF)
                .handler("mod","mod").done()
            ;
    }
}
