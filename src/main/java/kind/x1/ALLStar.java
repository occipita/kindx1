package kind.x1;

import kind.x1.ATN.State;
import kind.x1.ATN.ParseStateSet;
import kind.x1.ATN.ParseStateStack;
import kind.x1.ATN.SymbolLink;
import kind.x1.ATN.NonterminalLink;
import java.util.*;
import java.lang.reflect.Method;


public class ALLStar 
{
    private final ATN atn;
    private MarkableTokenStream input;
    private boolean debug;
    
    public ALLStar (ATN atn)
    {
        this.atn = atn;
    }
    
    public void setInput (TokenStream stream)
    {
        input = new MarkableTokenStream(stream); 
    }
    
    public ALLStar setDebug (boolean debug) 
    { 
        this.debug = debug; 
        if (debug) {
            System.out.println ("Debugging parser with ATN;");
            atn.dump();
            System.out.println ("Input:");
            input.debug = true;
            input.mark();
            Token t;
            while ((t = input.nextToken()).type() != TokenType.EOF)
                System.out.print("  "+t);
            input.rewind();
            System.out.println();
        }
        return this;
    }
    
    public Object parse (String symbol)
    {
        return parse (symbol, GSS.empty());
    }
    public Object parse (String symbol, GSS<State> stack)
    {
        int production = llPredict (symbol, stack);
        if (debug) System.out.println("Parse "+symbol+" predicted "+production);
        if (production < 0) return null;
        State s = atn.findState(symbol+"$"+production+".1");
        if (s == null) {
            System.err.println ("FATAL: failed to locate start state for symbol " + symbol + " production " + production);
            return null;
        }
        
        TreeMap<Integer,Object> args = new TreeMap<>();
        Map<Integer,ATN.ArgInitializer> inits = atn.initializers.get(production);
        if (inits != null)
            for (Map.Entry<Integer,ATN.ArgInitializer> init : inits.entrySet())
                switch(init.getValue()) {
                    case NONE: break;
                    case LIST: args.put(init.getKey(), new ArrayList()); break;
                    case OPTIONAL: args.put(init.getKey(), Optional.empty()); break;
                }
            
        while (!s.isAcceptState())
        {
            if (debug) System.out.println(s);
            if (!s.tokenLinks.isEmpty())
            {
                input.mark();
                Token t = input.nextToken();
                if (debug) System.out.println("Read token: "+t);
                SymbolLink l = s.tokenLinks.get(t.type());
                if (l != null) 
                {
                    s = l.target;
                    input.release();
                    updateArgs (args, l, t);
                    continue;
                }
                if (debug) System.out.println ("No match");
                input.rewind();
            }
            if (!s.nonterminalLinks.isEmpty())
            {
                if (s.nonterminalLinks.size()>1)
                    System.err.println("Warning: cannot process multiple nonterminal links within production @ " + s.getName());
                NonterminalLink l = s.nonterminalLinks.get(0);
                Object r = parse (l.nonterminal, stack.push(l.target));
                if (r == null) return null;
                s = l.target;
                updateArgs (args, l, r);
                continue;
            }
            // FIXME predicated links
            // FIXME mutator links
            if (!s.epsilonLinks.isEmpty())
            {
                if (s.epsilonLinks.size()>1)
                    System.err.println("Warning: cannot process multiple epsilon links within production @ " + s.getName());
                s = s.epsilonLinks.get(0);
                continue;
            }
            System.err.println("Error: failed to progress from state " + s.getName());
            return null;
        }
        if (debug) System.out.println (args);
        String[] handlerName = atn.handlers.get(production);
        if (handlerName == null || handlerName.length == 0) return args;
        List<Class<?>>[] argtypes = (List<Class<?>>[]) new List[args.size()];
        Object [] argvals = new Object[args.size()];
        for (int i = 0; i < argvals.length; i++)
        {
            argvals[i] = args.get(i);
            argtypes[i] = argvals[i] != null ? possibleTypeList(argvals[i].getClass()) : Arrays.asList(Object.class);
        }
        
        try
        {
            Object listener = atn.listener;
            for (int i = 0; i < handlerName.length - 1; i++)
            {
                Method getter = findMethod(listener.getClass(), handlerName[i], new List[0]);
                if (getter != null) listener = getter.invoke(listener, new Object[0]);
            }
            Method handler = findMethod(listener.getClass(), handlerName[handlerName.length - 1], argtypes);
            if (handler == null) {
                System.err.println("Failed to find handler " + Arrays.toString(handlerName) + " with types " + Arrays.toString(argtypes));
                return null;
            }
	    handler.setAccessible(true); // necessary even for public methods if the class itself isn't public!
            return handler.invoke (listener, argvals);
        }
        catch (Exception e) 
        {
            System.err.println ("Error invoking handler " + handlerName+": " + e);
            if (e.getCause() != null)
                System.err.println ("Caused by: " + e.getCause());
            e.printStackTrace();
            return null;
        }
    }
    
    private Method findMethod(Class<?> cls, String name, List<Class<?>>[] args)
    {
        Class<?>[] argarr = new Class<?>[args.length]; 
        for (List<Class<?>> candidateArgs : cartesianProduct(args))
        {
            try
            {
                return cls.getMethod (name, candidateArgs.toArray(argarr));
            } catch (NoSuchMethodException ignored) {}
        }
        return null;
    }
    private void updateArgs (Map<Integer,Object> args, SymbolLink link, Object value)
    {
        if (link.captureArg >= 0)
        {
            if (link.captureList)
                ((List)args.get (link.captureArg)).add (value);
            else if (link.captureOptional)
                args.put (link.captureArg, Optional.ofNullable(value));
            else
                args.put (link.captureArg, value);
        }
    }
    
    static List<Class<?>> possibleTypeList(Class<?> t)
    {
        List<Class<?>> r = new ArrayList<>();
        while (t != null)
        {
            r.add(t);
            for (Class<?> i : t.getInterfaces())
                r.add(i);
            t = t.getSuperclass();
        }
        return r;
    }
    
    static <T> List<LinkedList<T>> cartesianProduct(Collection<T>... args)
    {
        List<LinkedList<T>> result = new ArrayList<>();
        if (args.length == 0)
            result.add (new LinkedList<T>());
        else if (args.length == 1) 
            for (T a : args[0])
            {
                LinkedList<T> e = new LinkedList<>();
                e.add(a);
                result.add(e);
            }
        else 
        {
            Collection<T>[] rest = Arrays.copyOfRange(args,1,args.length);
            for (T a : args[0])
                for (LinkedList<T> b : cartesianProduct(rest))
                {
                    b.addFirst(a);
                    result.add(b);
                }
        }
        return result;
    } 
    
    public int llPredict (String symbol, GSS<State> parseStack)
    {
        input.mark();
        ParseStateSet current = atn.startState (symbol,parseStack);
        int nextToken = input.nextToken().type();
        while (current.size() > 0)
        {
            // if all viable paths to the current parse point have the same production as the first step,
            // that is the production to predict:
            int prediction = current.predictsProduction();
            if (prediction >= 0) {
                input.rewind();
                return prediction;   
            }
            // FIXME detect ambiguous production sets (where all possible state&stack pairs can be generated
            // by starting from multiple productions)
            
            // otherwise, get the next token and calculate the set of states we can reach by consuming it:
            int tokenType = nextToken;
            if (debug) System.out.println ("Next token: " + TokenType.getName(tokenType));
            nextToken = input.nextToken().type();
            
            current = atn.closure (current.move(tokenType), nextToken);
            current.removeIrrelevantStates(); // not included in tech report, but apparently necessary for conflict detection;
                                              // see comment at definition of this method for more detail
            if (debug) {
                for (ParseStateStack p : current)
                    System.out.print ("<"+p.getParseState().getState().getName()+","+p.getParseState().getProduction()+","+p.getStack()+"> ");
                System.out.println ();
            }
            Set<TreeSet<Integer>> conflicts = conflictSetsPerLoc(current);
            if (debug) System.out.println ("Conflict sets: " + conflicts);
            if (conflicts.size() == 1)
            {
                TreeSet<Integer> conflictProds = conflicts.iterator().next();
                if (conflictProds.size() > 1)
                {
                    // detected ambiguous parse - choose minimum production number in ambiguous set
                    input.rewind();
                    return conflictProds.first();  
                }
            }
        }
        input.rewind();
        return -1;
    }
    
    public Set<TreeSet<Integer>> conflictSetsPerLoc (ParseStateSet s)
    {
        HashMap<Pair<State,GSS<State>>,TreeSet<Integer>> css = new HashMap<>();
        for (ParseStateStack p : s)
        {
            Pair<State,GSS<State>> k = new Pair<>(p.getParseState().getState(),p.getStack());
            TreeSet<Integer> c =css.get(k);
            if (c == null)
            {
                c = new TreeSet<>();
                css.put(k,c);
            }
            c.add (p.getParseState().getProduction());
        }
        return new HashSet<>(css.values());
    }
}
