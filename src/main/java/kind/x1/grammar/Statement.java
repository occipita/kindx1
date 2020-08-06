package kind.x1.grammar;
import kind.x1.*;
import static kind.x1.TokenType.*;

public class Statement
{
    public static ATNBuilder apply (ATNBuilder builder)
    {
        return applyStmt(builder);
    }  
    public static ATNBuilder applyStmt (ATNBuilder builder)
    {
        return builder
            .addProduction ("stmt").s("expr").p(SEMICOLON).handler("stmt","expr").done()
            .addProduction ("stmt").p(KW_RETURN).s("expr").p(SEMICOLON).handler("stmt","ret").done()
            .addProduction ("stmt").p(KW_RETURN).p(SEMICOLON).handler("stmt","ret").done()
            .addProduction ("stmt").p(LBRACE).s("stmt-list").p(RBRACE).handler("stmt","block").done()
            .addProduction ("stmt").p(SEMICOLON).handler("stmt","nullStmt").done()
            .addProduction ("stmt")
                .s("id-list").p(COLON).s("typeexpr")
                    .beginOptional().p(EQUAL).so("expr").endOptional()
                    .p(SEMICOLON).handler("stmt","varDecl").done()
            .addProduction ("stmt")
                .s("id-list").p(COLON,EQUAL).s("expr").p(SEMICOLON).handler("stmt","varDecl").done()
            .addProduction ("stmt").p(KW_WHILE,LPAREN).s("expr").p(RPAREN).s("stmt").handler("stmt","whileStmt").done()
            .addProduction ("stmt").p(KW_IF,LPAREN).s("expr").p(RPAREN).s("stmt")
                .beginOptional().p(KW_ELSE).so("stmt").endOptional()
                .handler("stmt","ifStmt").done()
            
            // this production is used externally in locations where a completeblock is mandatory, eg fn definitions
            .addProduction ("stmt-block").p(LBRACE).s("stmt-list").p(RBRACE).handler("stmt","block").done()
            
            .addProduction ("stmt-list").s("stmt", "stmt-list").handler("prepend").done()
            .addProduction ("stmt-list") /* empty */ .handler("emptyLinkedList").done()
                        ;
    }
}
