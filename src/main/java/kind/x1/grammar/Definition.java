package kind.x1.grammar;

import kind.x1.*;
import static kind.x1.TokenType.*;

public class Definition 
{
    public static ATNBuilder apply (ATNBuilder builder)
    {
        return applyMisc(applyDefn(builder));
    }  
    public static ATNBuilder applyDefn (ATNBuilder builder)
    {
        return builder
            // properties
            .addProduction("defn")
                .s("id").p(COLON).s("typeexpr").beginOptional()
                    .p(EQUAL).so("expr")
                .endOptional().p(SEMICOLON).handler("defn","property").done()
            .addProduction("defn")
                .s("id").p(COLON,EQUAL).s("expr").p(SEMICOLON).handler("defn","property").done()
            .addProduction("defn")
                .s("id").beginOptional()
                    .p(COLON).so("typeexpr")
                .endOptional().p(LBRACE).s("prop-accessor-list").p(RBRACE)
                .handler("defn","property").done()
            // functions
            .addProduction("defn")
                .s("id").p(LPAREN).s("pattern-list").p(RPAREN)
                .beginOptional().p(COLON).so("typeexpr").endOptional()
                .s("stmt-block").handler("defn","fn").done()
            .addProduction("defn")
                .s("id").p(LPAREN).s("pattern-list").p(RPAREN)
                .beginOptional().p(COLON).so("typeexpr").endOptional()
                .p(ARROWR).s("expr").p(SEMICOLON).handler("defn","fn").done()
            // operators
            .addProduction("defn")
                .p(KW_OPERATOR).s("operator").p(LPAREN).s("pattern-list").p(RPAREN)
                .beginOptional().p(COLON).so("typeexpr").endOptional()
                .s("stmt-block").handler("defn","op").done()
            .addProduction("defn")
                .p(KW_OPERATOR).s("operator").p(LPAREN).s("pattern-list").p(RPAREN)
                .beginOptional().p(COLON).so("typeexpr").endOptional()
                .p(ARROWR).s("expr").p(SEMICOLON).handler("defn","op").done()
            // classes
            .addProduction("defn")
                .p(KW_CLASS).s("id")
                .beginOptional().p(LPAREN).so("typeargs").p(RPAREN).endOptional()
                .beginOptional().p(LSQBR).so("id").p(RSQBR).endOptional()
                .beginOptional().p(COLON).so("type-list").endOptional()
                .p(LBRACE).s("member-defn-list").p(RBRACE)
                .handler("defn","classDef").done()
            // polymorphic declarations
            .addProduction("defn")
                .p(KW_FORALL).s("typeargs","defn").handler("defn","forall").done()
            // interfaces
            .addProduction("defn")
                .p(KW_INTERFACE).s("id").beginOptional()
                    .p(LPAREN).so("typeargs").p(RPAREN)
                .endOptional().beginOptional()
                    .p(COLON).so("type-list")
                .endOptional().beginOptional()
                    .p(ARROWRD).so("id-list")
                .endOptional().p(LBRACE).s("member-defn-list").p(RBRACE)
                .handler("defn","iface").done()
            // implementations (standalone)
            .addProduction("defn")
                .p(KW_IMPLEMENTATION).s("id")
                .beginOptional().p(LPAREN).so("type-list").p(RPAREN).endOptional()
                .p(ARROWRD).s("type-list").p(LBRACE).s("member-defn-list").p(RBRACE)
                .handler("defn","impl").done()
            ;
    }  
    public static ATNBuilder applyMisc (ATNBuilder builder)
    {
        return builder                       
            // type arguments to forall, class or interface defs
            .addProduction("typeargs").s("id-list").beginOptional()
                .p(COLON).beginRepeating()
                    .sl("type-constraint")
                .repeatWithSeparator(COMMA).endOptional().handler("pair").done()
                
            // class members
            .addProduction("member-defn").s("defn").handler("copy").done()
            .addProduction("member-defn").s("member-attr-list-ne").s("member-defn").handler("defn","attr").done()
            // polymorphic declarations (repeated đfinition to allow forall clauses before class member attrs)
            .addProduction("member-defn")
                .p(KW_FORALL).s("typeargs","member-defn").handler("defn","forall").done()
            // abstract functions
            .addProduction("member-defn")
                .s("id").p(LPAREN).s("pattern-list").p(RPAREN,COLON).s("typeexpr").p(SEMICOLON)
                .handler("defn","abstractFn").done()         
            // abstract operators
            .addProduction("member-defn")
                .p(KW_OPERATOR).s("operator").p(LPAREN).s("pattern-list").p(RPAREN,COLON).s("typeexpr").p(SEMICOLON)
                .handler("defn","abstractOp").done()         
     
             // class member attributes
            .addProduction("member-attr").s(KW_PRIVATE).handler("copy").done()
            .addProduction("member-attr").s(KW_PROTECTED).handler("copy").done()
            .addProduction("member-attr").s(KW_PUBLIC).handler("copy").done()
            .addProduction("member-attr").s(KW_STATIC).handler("copy").done()
            .addProduction("member-attr").s(KW_FINAL).handler("copy").done()
            .addProduction("member-attr").s(KW_ABSTRACT).handler("copy").done()
            
            .addProduction("member-attr-list-ne").s("member-attr", "member-attr-list").handler("defn","attrListBuild").done()       
            .addProduction("member-attr-list").s("member-attr", "member-attr-list").handler("defn","attrListBuild").done()       
            .addProduction("member-attr-list") /* empty */ .handler("defn","emptyAttrList").done()
            
            .addProduction("member-defn-list").s("member-defn", "member-defn-list").handler("prepend").done()
            .addProduction("member-defn-list") /* empty */ .handler("emptyLinkedList").done()
            
            .addProduction("prop-accessor-list").s("prop-accessor", "prop-accessor-list").handler("prepend").done()
            .addProduction("prop-accessor-list") /* empty */ .handler("emptyLinkedList").done()
            
            .addProduction("prop-accessor").s("id").p(ARROWR).s("expr").p(SEMICOLON).handler("defn","propertyAccessor").done()
            .addProduction("prop-accessor").s("id","stmt-block").handler("defn","propertyAccessor").done()
            ;
    }
}
