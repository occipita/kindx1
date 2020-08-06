package kind.x1;

import java.util.*;

public class ATN 
{
    public static class SymbolLink
    {
        State target;
        int captureArg = -1;
        boolean captureList;
        boolean captureOptional;
        public SymbolLink (State t) { target = t; }
    }
    public static class NonterminalLink extends SymbolLink
    {
        String nonterminal;
        public NonterminalLink (String nt, State t) { super(t); nonterminal = nt; }
    }
    public class State
    {
        String name;
        int production;
        List<State> epsilonLinks = new ArrayList<>();
        Map<Integer,SymbolLink> tokenLinks = new TreeMap<>(); // FIXME capture vl!
        List<NonterminalLink> nonterminalLinks = new ArrayList<>();
        boolean acceptState;
        
        void addEpsilonLink (State target) { epsilonLinks.add (target); }
        SymbolLink addTokenLink (int tokenType, State target) 
        { 
            SymbolLink l = new SymbolLink(target);
            tokenLinks.put(tokenType, l);
            return l;
        }
        NonterminalLink addNonterminalLink (String name, State target) 
        { 
            NonterminalLink l = new NonterminalLink (name, target);
            nonterminalLinks.add (l); 
            return l;
        }     
        
        void dump ()
        {
            System.out.println("    "+name+": ["+getNonterminalName()+"]");
            if (acceptState) System.out.println ("        accept");
            for (State s : epsilonLinks)
                System.out.println ("        epsilon -> " + s.name);
            for (Map.Entry<Integer, SymbolLink> e : tokenLinks.entrySet())
                System.out.println ("        token " + TokenType.getName (e.getKey()) + " -> " + e.getValue().target.name);
            for (NonterminalLink l : nonterminalLinks)
                System.out.println ("        nt " + l.nonterminal + " -> " + l.target.name + " [" + l.captureArg + (l.captureList ? "*" : "") + "]");
        }
        
        public String getName() { return name; }
        public int getProduction () { return production; }
        public String toString() { return name; }
        public boolean isAcceptState () { return acceptState; }
        public String getNonterminalName ()
        {
            int delim= name.indexOf('$');
            int colon = name.indexOf(':');
            if (colon > 0 && (delim < 0 || colon < delim)) delim= colon;
            if (delim < 0) return name;
            return name.substring (0, delim);
        }
    }

    public static class ParseState 
    {
        final State state;
        final int production;
        
        public ParseState (State s, int p) { state = s; production = p; }        
        public State getState () { return state; }
        public int getProduction() { return production; }
        public int hashCode() { return state.hashCode() + 7 * production; }
        public boolean equals (Object o)
        {
            if (((ParseState)o).state != state) return false;
            if (((ParseState)o).production != production) return false;
            return true;
        }
        public String toString() { return "<" + state.getName() + "," + production + ">"; }
        public ParseState moveTo (State t){ 
            if (t==null) throw new IllegalArgumentException("moveTo null (from "+state+")");
            return new ParseState (t, production); 
        }
    }
    public static class ParseStateStack
    {
        final ParseState parseState;
        final GSS<State> stack;
        public ParseStateStack (ParseState p, GSS<State> s) { parseState = p; stack = s; }
        public ParseState getParseState () { return parseState; }
        public GSS<State> getStack() { return stack; }
        public int hashCode () { return parseState.hashCode() + 13*stack.hashCode(); }
        public boolean equals (Object o)
        {
            if (!((ParseStateStack)o).parseState.equals(parseState)) return false;
            if (!((ParseStateStack)o).stack.equals(stack)) return false;
            return true;
        }
        public ParseStateStack moveTo (State t)
        {
            return new ParseStateStack(parseState.moveTo(t), getStack());
        }
        public ParseStateStack moveToPushing (State t, State p)
        {
            return new ParseStateStack(parseState.moveTo(t), getStack().push(p));
        }
    }
    public static class ParseStateSet implements Iterable<ParseStateStack> 
    {
        final Map<ParseState, GSS<State>> states;
        
        public ParseStateSet (Map<ParseState, GSS<State>> s) { states = s; }
        public ParseStateSet () { states = new HashMap<>(); }
        public int size() { return states.size(); }
        public Iterator<ParseStateStack> iterator () { 
            final Iterator<Map.Entry<ParseState, GSS<State>>> i = states.entrySet().iterator(); 
            return new Iterator<ParseStateStack>() {
                public boolean hasNext() { return i.hasNext(); }
                public ParseStateStack next () { 
                    Map.Entry<ParseState, GSS<State>> e = i.next();
                    return new ParseStateStack (e.getKey(), e.getValue());
                }
                public void remove () { i.remove(); }
            };
        }
        public int hashCode() { return states.hashCode(); }
        public boolean equals (Object o) { return states.equals (((ParseStateSet)o).states); }
        public String toString () { return states.toString(); } 
        public ParseStateStack[] toArray (Comparator<ParseStateStack> sortedBy)
        {
            ParseStateStack[] result = new ParseStateStack[size()];
            int i = 0;
            for (ParseStateStack pss : this) result[i++] = pss;
            if (sortedBy != null) Arrays.sort(result, sortedBy);
            return result; 
        }
        public ParseStateSet move (int tokenType)
        {
            ParseStateSet r = new ParseStateSet();
            for (ParseStateStack s : this)
            {
                SymbolLink t = s.getParseState().getState().tokenLinks.get(tokenType);
                if (t != null) r.add (s.getParseState().moveTo(t.target), s.getStack());
                if (s.getParseState().getState().isAcceptState() && s.getStack().isEmpty()) // allow anythingto follow initial symbol
                    r.add(s.getParseState(), s.getStack());
            }
            return r;
        }
        
        public void add (ParseState parseState, GSS<State> stack)
        {
            // merge stack if state already present
            GSS<State> cur = states.get(parseState);
            if (cur != null)
            {
                states.put(parseState, GSS.merge(cur, stack));
            }
            else // otherwiseadd to map:
            {
                states.put(parseState, stack);
            }
        }
        
        /** returns the unique production predicted by the current set, or -1 if no such production exists */
        public int predictsProduction ()
        {
            Iterator<ParseState> i = states.keySet().iterator(); 
            if (!i.hasNext()) return -1;
            int prod = i.next().getProduction();
            while (i.hasNext())
                if (i.next().getProduction() != prod) return -1;
            return prod;
        }
            
        /** Removes from the set any states which do not have a defined transition on a terminal symbol
         * and which are not accept states.
         * These states are irrelevant to subsequent move operations, and appear to cause failure of
         * the conflict detection code (although this is not described in the ALL(*) technical report
         * examples of configurations given appear to have had this operation performed, and when
         * not performed these unwanted states cause failure to identify ambiguities) */
        public void removeIrrelevantStates()
        {
            Iterator<ParseState> i = states.keySet().iterator(); 
            while (i.hasNext())
            {
                State s = i.next().getState();
                if (s.tokenLinks.isEmpty() && !s.isAcceptState()) i.remove();
            }
        }
    }

    public static final Comparator<ParseStateStack> PSS_SORTBYSTATENAME = new Comparator<ParseStateStack>() {
        public int compare (ParseStateStack a, ParseStateStack b) { 
            return a.getParseState().getState().getName().compareTo(b.getParseState().getState().getName());
        }
    };
    
    public enum ArgInitializer {
        NONE, LIST, OPTIONAL
    }
    private Map<String, State> states = new TreeMap<>(); // so we can dump the states in a useful order
    private Map<String, List<State>> returnSites = new HashMap<>();
    Object listener;
    Map<Integer,String[]> handlers = new TreeMap<>();
    Map<Integer,Map<Integer,ArgInitializer>> initializers = new TreeMap<>();
    
    State newState (String name, int production)
    {
        State r = new State();
        r.name = name;
        r.production = production;
        states.put (name, r);
        return r;   
    }
    
    void addReturnSite (String nonterm, State returnTo)
    {
        List<State> l = returnSites.get(nonterm);
        if (l == null)
        {
            l = new ArrayList<>();
            returnSites.put (nonterm, l);
        }
        l.add (returnTo);
    }
    
    State findState (String name)
    {
        return states.get(name);
    }
    
    void setInitializer(int production, int arg, ArgInitializer val)
    {
        Map<Integer,ArgInitializer> pm = initializers.get(production);
        if (pm == null) {
            pm = new TreeMap<>();
            initializers.put(production,pm);
        }
        pm.put(arg,val);
    }
    
    public void dump ()
    {
        System.out.println ("ATN with " + states.size() + " states:");
        for (State s : states.values()) s.dump();
    }
    
    public ParseStateSet startState (String nonterminal, GSS<State> stack)
    {
        State start = findState(nonterminal);
        if (start == null)     
            return new ParseStateSet(Collections.emptyMap()); // will cause error
        Map<ParseState,GSS<State>> prodStarts = new HashMap<>();
        for (State s : start.epsilonLinks)
            prodStarts.put (new ParseState (s, s.production), stack);
        return closure (new ParseStateSet(prodStarts), -1); 
    }
    
    public List<State> getReturnSites (String nonterm)
    {
        List<State> r = returnSites.get (nonterm);
        if (r == null) return Collections.emptyList();
        return r;
    }
    
    public void closure (Set<ParseStateStack> busy, ParseStateStack c, ParseStateSet dest, int nextTokenType)
    {
        if (busy.contains(c)) return;
        busy.add(c);
        dest.add(c.getParseState(), c.getStack());
        
        if (c.getParseState().getState().isAcceptState())
        {
            if (c.getStack().isWildcard())
                for (State s : getReturnSites (c.getParseState().getState().getNonterminalName()))
                    closure (busy, c.moveTo(s), dest, nextTokenType);
            for (GSS.Entry<State> e : c.getStack()) 
                closure(busy, new ParseStateStack (new ParseState (e.getHead(), c.getParseState().getProduction()), e.getTails()), dest, nextTokenType);
        }
        for (NonterminalLink l : c.getParseState().getState().nonterminalLinks) closure (busy, c.moveToPushing(findState(l.nonterminal), l.target), dest, nextTokenType);
        // we follow epsilon links from a state iff there are no token links that will be followed in the next move operation,
        // as this is the way the parse function must behave in order to ensure transitions are deterministic
        if (!c.getParseState().getState().tokenLinks.containsKey(nextTokenType))
            for (State t : c.getParseState().getState().epsilonLinks) closure(busy, c.moveTo(t), dest, nextTokenType);
        // FIXME predicated links
        // FIXME mutator links
    }
    
    public ParseStateSet closure (ParseStateSet s, int nextTokenType)
    {
        ParseStateSet r = new ParseStateSet();
        for(ParseStateStack pss : s) closure(new HashSet<>(), pss, r, nextTokenType);
        return r;
    }
    
}
