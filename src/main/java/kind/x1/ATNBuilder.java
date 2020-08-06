package kind.x1;

import java.util.*;

public class ATNBuilder 
{
    ATN building = new ATN();
    int productionCount = 0;
    int lowPriorityProductionCount = 0;
    final int LPPROD_START = 90000;
    
    public class Production
    {
        String stateNamePrefix;
        int stateCount = 0;
        int number;
        ATN.State current;
        ATN.State next;
        ATN.State accept;
        ATN.State optionalStart, repeatStart; // nb would need a stack to handle nested optionals etc...
        List<ATN.SymbolLink> lastLink;
        int nextArg = 0;
        
        ATN.State newState ()
        {
            if (next != null) return next;
            return next = building.newState(stateNamePrefix+"."+(++stateCount), number);
        }
        void move ()
        {
            current = next;
            next = null;
        }
        public Production token (int tokenType)
        {
            lastLink = Collections.singletonList(current.addTokenLink(tokenType, newState()));
            move();
            return this;
        }
        public Production handler (String... name)
        {
            building.handlers.put (number, name);
            return this;
        }
        public ATNBuilder done ()
        {
            current.addEpsilonLink(accept);
            return ATNBuilder.this;
        }
        public Production anyOf(int... tokenTypes)
        {
            newState();
            lastLink = new ArrayList<>(tokenTypes.length);
            for (int tokenType : tokenTypes)
                lastLink.add(current.addTokenLink(tokenType, next));
            move();
            return this;
        }
        public Production beginOptional()
        {
            if (optionalStart != null) throw new IllegalArgumentException("Cannot nest optionals @" + current.getName());
            optionalStart = current;
            return this;
        }
        public Production endOptional()
        {
            optionalStart.addEpsilonLink(current);
            optionalStart = null;
            return this;
        }
        public Production beginRepeating()
        {
            if (repeatStart != null) throw new IllegalArgumentException("Cannot nest repetitions @" + current.getName());
            repeatStart = current;
            return this;
        }
        public Production repeatWithSeparator(int... tokenTypes)
        {
            lastLink = new ArrayList<>(tokenTypes.length);
            for (int tokenType : tokenTypes)
                lastLink.add(current.addTokenLink(tokenType, repeatStart));
            repeatStart = null;
            return this;
        }
        public Production repeatUntilTerminator(int... tokenTypes)
        {
            current.addEpsilonLink(repeatStart);
            newState();
            lastLink = new ArrayList<>(tokenTypes.length);
            for (int tokenType : tokenTypes)
                lastLink.add(current.addTokenLink(tokenType, next));
            repeatStart = null;
            move();
            return this;
        }
        public Production nonterminal (String name)
        {
            lastLink = Collections.singletonList(current.addNonterminalLink (name, newState()));
            building.addReturnSite (name, next);
            move();
            return this;
        }
        public Production asArg (int index)
        {
            for (ATN.SymbolLink ll: lastLink)
                ll.captureArg = index;
            return this;
        } 
        public Production asListArg (int index)
        {
            building.setInitializer (number, index, ATN.ArgInitializer.LIST);
            for (ATN.SymbolLink ll: lastLink)
            {
                ll.captureArg = index;
                ll.captureList = true;
            }
            return this;
        }
        public Production asOptionalArg (int index)
        {
            building.setInitializer (number, index, ATN.ArgInitializer.OPTIONAL);
            for (ATN.SymbolLink ll: lastLink)
            {
                ll.captureArg = index;
                ll.captureOptional = true;
            }
            return this;
        } 
        /** Shortcut for multiple token(int) or nonterminal(String) calls, identified by arg type */
        public Production p (Object... syms)
        {
            for (Object s : syms)
                if (s instanceof Integer)
                    token((int)(Integer)s);
                else
                    nonterminal((String)s);
            return this;
        }
        /** As #p(Object...) but also capturing autonumbered args */
        public Production s (Object... syms)
        {
            for (Object s : syms)
            {
                if (s instanceof Integer)
                    token((int)(Integer)s);
                else
                    nonterminal((String)s);
                asArg(nextArg++);
            }
            return this;
        }
        /** As #p(Object...) but also capturing autonumbered list args */
        public Production sl (Object... syms)
        {
            for (Object s : syms)
            {
                if (s instanceof Integer)
                    token((int)(Integer)s);
                else
                    nonterminal((String)s);
                asListArg(nextArg++);
            }
            return this;
        }
        /** As #p(Object...) but also capturing autonumbered optional args */
        public Production so (Object... syms)
        {
            for (Object s : syms)
            {
                if (s instanceof Integer)
                    token((int)(Integer)s);
                else
                    nonterminal((String)s);
                asOptionalArg(nextArg++);
            }
            return this;
        }
    }

    public Production addProduction(String name)
    {
        return addProduction(name, (++productionCount));
    }
    public Production addLowPriorityProduction(String name)
    {
        return addProduction(name, LPPROD_START + (++lowPriorityProductionCount));
    }
    private Production addProduction(String name, int number)
    {
        Production r = new Production();
        r.number = number;
        ATN.State start = building.findState(name);
        if (start == null)
        {
            start = building.newState(name, -1);   
            r.accept = building.newState(name+":accept", -1);
            r.accept.acceptState = true;
        }
        else
            r.accept = building.findState(name+":accept");
        r.stateNamePrefix = name+"$"+r.number;
        start.addEpsilonLink (r.newState());
        r.move();
        return r;
    }
        
    public ATNBuilder withListener (Object listener)
    {
        building.listener = listener;
        return this;
    }    
    
    public ATN build ()
    {
        return building;
    }
}
