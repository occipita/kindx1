package kind.x1;

import static kind.x1.TokenType.*;
import java.util.HashSet;

public class ATNTest extends Assertions implements Runnable
{
    public void run()
    {
        testSimpleStartState();
        testMove();
        testClosureEpsilon();
        testClosureNonterminal();
        testClosureReturnStack();
        testClosureReturnWild();
    }
    public void testSimpleStartState()
    {
        ATN atn = simpleATN();
        ATN.ParseStateSet pss = atn.startState("start", GSS.empty());
        assertEqual ("testSimpleStartState: size", pss.size(), 1);
        ATN.ParseState ps = pss.iterator().next().getParseState();
        assertEqual ("testSimpleStartState: first state name", ps.getState().getName(), "start$1.1");
        assertEqual ("testSimpleStartState: first state prod", ps.getProduction(), 1);
    }
    public void testMove()
    {
        ATN atn = simpleATN();
        ATN.ParseStateSet pss = atn.startState("start", GSS.empty()).move(ID);
        assertEqual ("testMove: size", pss.size(), 1);
        ATN.ParseState ps = pss.iterator().next().getParseState();
        assertEqual ("testMove: first state name", ps.getState().getName(), "start$1.2");
        assertEqual ("testMove: first state prod", ps.getProduction(), 1);
    }
    public void testClosureEpsilon()
    {
        ATN atn = new ATNBuilder()
            .addProduction("start").token(ID).beginOptional().token(ID).endOptional().token(INTLITERAL).handler("buildPair").done()
            .withListener(new DefaultListener())
            .build();
        ATN.ParseStateSet pss = atn.startState("start", GSS.empty()).move(ID);
        pss = atn.closure(pss, -1);
        assertEqual ("testClosureEpsilon: size", pss.size(), 2);
        ATN.ParseStateStack [] a = pss.toArray(ATN.PSS_SORTBYSTATENAME);
        assertEqual ("testClosureEpsilon: first state name", a[0].getParseState().getState().getName(), "start$1.2");
        assertEqual ("testClosureEpsilon: second state name", a[1].getParseState().getState().getName(), "start$1.3");
    }
    public void testClosureNonterminal()
    {
        ATN atn = atnWithSubDef();
        ATN.ParseStateSet pss = atn.startState("start", GSS.empty()).move(ID);
        pss = atn.closure(pss, -1);
        assertEqual ("testClosureNonterminal: size", pss.size(), 4);
        ATN.ParseStateStack [] a = pss.toArray(ATN.PSS_SORTBYSTATENAME);
        assertEqual ("testClosureNonterminal: state 0 name", a[0].getParseState().getState().getName(), "start$1.2");
        assertEqual ("testClosureNonterminal: state 1 name", a[1].getParseState().getState().getName(), "sub");
        assertEqual ("testClosureNonterminal: state 2 name", a[2].getParseState().getState().getName(), "sub$2.1");
        assertEqual ("testClosureNonterminal: state 3 name", a[3].getParseState().getState().getName(), "sub$3.1");
        assertEqual ("testClosureNonterminal: sub state stack top", a[1].getStack().iterator().next().getHead().getName(), "start$1.3");
    }
    public void testClosureReturnStack()
    {
        ATN atn = atnWithSubDef();
        ATN.ParseStateSet pss = atn.startState("start", GSS.empty()).move(ID);
        pss = atn.closure(pss, INTLITERAL).move(INTLITERAL).move(ID);
        pss = atn.closure(pss, -1);
        assertEqual ("testClosureReturnStack: size", pss.size(), 3);
        ATN.ParseStateStack [] a = pss.toArray(ATN.PSS_SORTBYSTATENAME);
        assertEqual ("testClosureReturnStack: state 0 name", a[0].getParseState().getState().getName(), "start$1.3");
        assertEqual ("testClosureReturnStack: state 1 name", a[1].getParseState().getState().getName(), "sub$2.3");
        assertEqual ("testClosureReturnStack: state 2 name", a[2].getParseState().getState().getName(), "sub:accept");
        assertTrue ("testClosureReturnStack: sub state stack should be empty", a[0].getStack().isEmpty());
    }
    public void testClosureReturnWild()
    {
        ATN atn = atnWithSubDef();
        //atn.dump();
        ATN.ParseStateSet pss = atn.startState("sub", GSS.wildcard()).move(INTLITERAL).move(ID);
        pss = atn.closure(pss, -1);
        assertEqual ("testClosureReturnWild: size", pss.size(), 3);
        ATN.ParseStateStack [] a = pss.toArray(ATN.PSS_SORTBYSTATENAME);
        assertEqual ("testClosureReturnWild: state 0 name", a[0].getParseState().getState().getName(), "start$1.3");
        assertEqual ("testClosureReturnWild: state 1 name", a[1].getParseState().getState().getName(), "sub$2.3");
        assertEqual ("testClosureReturnWild: state 2 name", a[2].getParseState().getState().getName(), "sub:accept");
        assertTrue ("testClosureReturnWild: sub state stack should be wild", a[0].getStack().isWildcard());
    }
    
    class DefaultListener
    {
        public String buildPair (Object a, Object b) { return "<"+a+","+b+">"; }
    }
    private ATN simpleATN ()
    {
        return new ATNBuilder()
            .addProduction("start").token(ID).token(ID).handler("buildPair").done()
            .withListener(new DefaultListener())
            .build();
    }
    private ATN atnWithSubDef()
    {
        return new ATNBuilder()
            .addProduction("start").token(ID).nonterminal("sub").token(SEMICOLON).handler("buildPair").done()
            .addProduction("sub").token(INTLITERAL).token(ID).handler("buildPair").done()
            .addProduction("sub").token(ID).token(STRINGLITERAL).handler("buildPair").done()
            .withListener(new DefaultListener())
            .build();
    }
            
}
