package kind.x1;

import static kind.x1.TokenType.*;
import java.io.Reader;

public class Lexer implements TokenStream
{
    NDFA ndfa;

    private final static int STRSPECIAL = 0;
    private final static String STRSPECIAL_SET = "\"\\";
    private final static int ESCAPECHARS = 1;
    private final static String ESCAPECHARS_SET = "\"\\'rn0gtf";
    private final static int OPERATORSYMBOL = 2;
    private final static String OPERATORSYMBOL_SET = "!£$%^&*+-=#~@?/><|";
    
    public Lexer ()
    {
        ndfa = stage2(stage1(new NDFABuilder().maxStates(16)
                                .newState("start").start())).build();
        createSets();
    }
    private NDFABuilder stage1(NDFABuilder in)
    {
        return in
            .onSpecial(-1,-1).accept(EOF)                   // handle EOF
            .from("start")
                .oneOrMore().special(NDFA.WS).done()        // handle whitespace
                .lazyAccept(WS)
            .from("start")                                  // handle comments
                .on('/').label("comment_start")
                // mukti line comments:
                .on('*').linkToSelf().special(NDFA.ANY).done().onStr("*/").accept(COMMENT)
                //single line comments:
                .from("comment_start")
                .on('/').linkToSelf().special(NDFA.ANY).done().on('\n').accept(COMMENT)
            .from("start")                                  // numeric literal with specified base
                .on('0').label("literal_base")
                // hex literals
                .onIns('x')  // start of hex (expecting digit)
                    .oneOrMore().special(NDFA.DIGIT).rangeIns('a','f').done()
                    .label("hexliteral_main").lazyAccept(HEXLITERAL)
                    .on('_') // grouping separator
                        .linkTo("hexliteral_main").special(NDFA.DIGIT).rangeIns('a','f').done()
                // binary literals
                .from("literal_base").onIns('b')  // start of bin (expecting digit)
                    .oneOrMore().range('0','1').done()
                    .label("binliteral_main").lazyAccept(BINLITERAL)
                    .on('_') // grouping separator
                        .linkTo("binliteral_main").range('0','1').done()
                // octal literals
                .from("literal_base").onIns('o')  // start of octal (expecting digit)
                    .oneOrMore().range('0','7').done()
                    .label("octliteral_main").lazyAccept(OCTLITERAL)
                    .on('_') // grouping separator
                        .linkTo("octliteral_main").range('0','7').done()
            .from("start")                        // integer literals
                .oneOrMore().special(NDFA.DIGIT).done()
                    .label("intliteral_main").lazyAccept(INTLITERAL)
                    .on('_') // grouping separator
                        .linkTo("intliteral_main").special(NDFA.DIGIT).done()
            .from("start")                        // float literals
                .oneOrMore().special(NDFA.DIGIT).done()
                    .label("floatliteral_whole")
                    .on('_') // grouping separator
                        .linkTo("floatliteral_whole").special(NDFA.DIGIT).done()
                    .on('.') // expecting fractional part
                    .oneOrMore().special(NDFA.DIGIT).done()
                    .label("floatliteral_frac").lazyAccept(FLOATLITERAL)
                    .on('_') // grouping separator
                        .linkTo("floatliteral_frac").special(NDFA.DIGIT).done()
                    .onIns('e')
                        .label("floatliteral_exp_start")
                        .oneOrMore().special(NDFA.DIGIT).done()
                        .label("floatliteral_exp").lazyAccept(FLOATLITERAL)
                        .on('_') // grouping separator
                            .linkTo("floatliteral_exp").special(NDFA.DIGIT).done()
                     .from("floatliteral_whole").linkTo("floatliteral_exp_start").ins('e').done()
                     .from("floatliteral_exp_start").onAnyOf().ch('+').ch('-').done()
                         .linkTo("floatliteral_exp").special(NDFA.DIGIT).done()
            .from("start").on('-').label("negative_literal")    // negative numbers
                .linkTo("intliteral_main").special(NDFA.DIGIT).done()
                .from("negative_literal").linkTo("floatliteral_whole").special(NDFA.DIGIT).done()
                .from("negative_literal").linkTo("literal_base").ch('0').done()
            .from("start").on('"').label("stringliteral_main")    // stringliterals
                .linkToSelf().special(NDFA.ANY,NDFA.CUSTOM+STRSPECIAL).done()
                .on('"').accept(STRINGLITERAL)
                .from("stringliteral_main").on('\\').label("stringliteral_escape")
                    .linkTo("stringliteral_main").special(NDFA.CUSTOM+ESCAPECHARS).done()
                    .from("stringliteral_escape").on('u')
                        .onAnyOf().special(NDFA.DIGIT).rangeIns('a','f').done()
                        .onAnyOf().special(NDFA.DIGIT).rangeIns('a','f').done().label("stringliteral_eschex2")
                        .onAnyOf().special(NDFA.DIGIT).rangeIns('a','f').done()
                        .linkTo("stringliteral_main").special(NDFA.DIGIT).rangeIns('a','f').done()
                    .from("stringliteral_escape").linkTo("stringliteral_eschex2").ch('x').done()    
            .from("start")                                        // identifiers
                .oneOrMore().special(NDFA.LETTER).done().lazyAccept(ID)
                .linkToSelf().special(NDFA.DIGIT).ch('_').done();
    }
    private NDFABuilder stage2(NDFABuilder in)
    {
        return in
            // generic operators (precedence may be overridden by laterentries)
            .from("start").oneOrMore().special(NDFA.CUSTOM+OPERATORSYMBOL).done()
                .label("custom_op_anystart").lazyAccept(OP_MISC)
            .from("start").onAnyOf().ch('+').ch('-').done().lazyAccept(OP_ARITH_S)
                .oneOrMore().special(NDFA.CUSTOM+OPERATORSYMBOL).done().lazyAccept(OP_ARITH_S)
            .from("start").on('*').lazyAccept(ASTERISK)
                .oneOrMore().special(NDFA.CUSTOM+OPERATORSYMBOL).done().lazyAccept(OP_ARITH_P)
            .from("start").on('/').lazyAccept(OP_ARITH_P)
                .oneOrMore().special(NDFA.CUSTOM+OPERATORSYMBOL).done().lazyAccept(OP_ARITH_P)
            .from("custom_op_anystart").on('=').lazyAccept(OP_ASSIGN)
            .from("custom_op_anystart").on('>').lazyAccept(OP_CHAIN)
            .from("start").on('<').oneOrMore().special(NDFA.CUSTOM+OPERATORSYMBOL).done().lazyAccept(OP_CHAIN)
            .from("start").on('\\').oneOrMore().special(NDFA.LETTER).done().label("named_op_main")
                .on('\\').accept(OP_MISC)
                .from("named_op_main").on(':').accept(OP_PRE)
                .from("named_op_main").on('!').accept(OP_POST)
            // specific operators
            .from("start").onStr("++").lazyAccept(DOUBLEPLUS)
            .from("start").onStr("--").lazyAccept(DOUBLEMINUS)
            .from("start").on('|').lazyAccept(OP_BIT_S).on('|').lazyAccept(OP_LOG_S)
            .from("start").on('^').lazyAccept(OP_BIT_S).on('^').lazyAccept(OP_LOG_S)
            .from("start").on('&').lazyAccept(OP_BIT_P).on('&').lazyAccept(OP_LOG_P)
            .from("start").onAnyOf().ch('~').ch('!').done().lazyAccept(OP_PRE)
            .from("start").on('%').lazyAccept(OP_ARITH_P)
            .from("start").on('?').lazyAccept(OP_CONDL).on('?').lazyAccept(OP_CONDR)
            .from("start").onStr(">>").lazyAccept(OP_ARITH_SH)
            .from("start").onStr("<<").lazyAccept(OP_ARITH_SH)
            .from("start").onStr("**").lazyAccept(OP_ARITH_I)
            .from("start").onStr("\\>").lazyAccept(OP_CHAIN)
            .from("start").onStr("<-").lazyAccept(ARROWL)
            .from("start").onStr("->").lazyAccept(ARROWR)
            .from("start").onStr("=>").lazyAccept(ARROWRD)
            .from("start").onAnyOf().ch('<').ch('>').done().lazyAccept(OP_COMP)
                .on('=').lazyAccept(OP_COMP)
            .from("start").onAnyOf().ch('!').ch('=').done()
                .on('=').lazyAccept(OP_COMP)
            
            // single char tokens
            .from("start").on('_').lazyAccept(UNDERSCORE)
            .from("start").on('=').lazyAccept(EQUAL)
            .from("start").on(':').lazyAccept(COLON).on(':').lazyAccept(DBLCOLON)
            .from("start").on('\\').lazyAccept(BACKSLASH)
            .from("start").on('[').lazyAccept(LSQBR)
            .from("start").on(']').lazyAccept(RSQBR)
            .from("start").on('-').lazyAccept(MINUS)
            .from("start").on('(').accept(LPAREN)
            .from("start").on(')').accept(RPAREN)
            .from("start").on('{').accept(LBRACE)
            .from("start").on('}').accept(RBRACE)
            .from("start").on('.').accept(DOT)
            .from("start").on(';').accept(SEMICOLON)
            .from("start").on(',').accept(COMMA)
             ;
    }
    private void createSets()
    {
        ndfa.setCustomCharSet (STRSPECIAL, STRSPECIAL_SET);
        ndfa.setCustomCharSet (ESCAPECHARS, ESCAPECHARS_SET);        
        ndfa.setCustomCharSet (OPERATORSYMBOL, OPERATORSYMBOL_SET);       
    }
    public void setInput (String str)
    {
        ndfa.setInput (str);
    }
    public void setInput (Reader in)
    {
        ndfa.setInput (in);
    }
    public Token nextToken ()
    {
        return ndfa.nextToken ();
    }
    public void setDebug (boolean d) { ndfa.setDebug(d); }
}
