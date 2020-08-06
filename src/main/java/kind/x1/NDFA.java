package kind.x1;

import java.io.Reader;
import java.util.Arrays;

/**
 * A simple implementation of a non-deterministic finite state automaton
 * for identification of character sequences (eg tokenization)
 * using arrays for both active state set and state transition tables.
 * Supports both eager accept states (terminating recognition as soon as they
 * are reached) and lazy accept states (terminating recognition only if no 
 * further symbols can be matched) with backtracking (although only to
 * a single recognised token). Active state set size is limited and must br
 * specified during c0nstruction. This class is intentionally simple rather
 * than flexible or efficient. For non-experimental applications, translation
 * to a deterministic FSA is recommended.
 */
public class NDFA implements TokenStream 
{
    public static final int EAGER = 1;
    public static final int LAZY = 2;
    public static final int CHARCLASS_BASE = 1 << 16;
    public static final int LETTER = CHARCLASS_BASE;
    public static final int DIGIT = CHARCLASS_BASE + 1;
    public static final int ANY = CHARCLASS_BASE + 2;
    public static final int WS = CHARCLASS_BASE + 3;
    public static final int CUSTOM = 1 << 17;

    public static final int UPDATE_NORMAL = 0;
    public static final int UPDATE_EAGER_ACCEPT = 1;
    public static final int UPDATE_NO_VALID_STATES = 3;
    public static final int UPDATE_ERROR = 4;

    
    private final static Token[] EMPTY = new Token[0];
     
    private class StateSet
    {
        private int[] currentStates; 
        private int stateCount; 
        private Token[] accepted = EMPTY;
        private StateSet backtrack;   
        private StringBuilder text = new StringBuilder();
        
        public StateSet()
        {
            currentStates = new int[maxStates];
            currentStates[0] = initState;
            stateCount = 1;
        }
        boolean hasState(int s)
        {
            for (int i = 0; i < stateCount; i++)
                if (currentStates[i] == s) return true;
            return false;
        }
        void emit (int state, String text)
        {
            int i = accepted.length;
            accepted = Arrays.copyOfRange(accepted,0,i+1);
            accepted[i] = new Token(stateMap[state][1], text);
            backtrack = null; // an eager accept overrides pending lazy acceepts
            currentStates = new int[maxStates]; // and restartsa h initial state
            currentStates[0] = initState;
            stateCount = 1;
            this.text = new StringBuilder();
        }
        void clearFirst ()
        {
            accepted = Arrays.copyOfRange(accepted, 1, accepted.length);
        }
        boolean emitPending () { return accepted.length > 0; }
        boolean backtrackPending () { return stateCount == 0 && backtrack != null && !emitPending(); }
        boolean canContinue () { return stateCount > 0; }
        int update (int ch)
        {
            if (backtrack != null)
                backtrack.update (ch);
            text.append ((char)ch);
                
            int curStateCount = stateCount; //protect against this changing during update
            boolean [] delStates = new boolean [stateCount]; // defer deletion until after processing
            for (int i = 0; i < curStateCount; i++)
            {
                int [] map = stateMap[currentStates[i]];
                boolean found = false;
                for (int j = 2; j < map.length; j += 3)
                    if ((map[j] <= ch && map[j+1] >= ch) || 
                        (map[j] >= CHARCLASS_BASE && classMatch (map[j], map[j+1], ch)))
                    {
                        int newState = map[j+2];
                        int s = found ? stateCount++ : i;
                        currentStates[s] = newState;
                        found = true;
                        
                        if ((stateMap[newState][0] & EAGER) == EAGER) 
                        {
                            emit(newState, text.toString());
                            return UPDATE_EAGER_ACCEPT;
                        }
                    }
                if (!found) delStates[i] = true;
            }
            
            int d = 0;
            int lazyAcceptor = -1;
            for (int s = 0; s < stateCount; s++)
            {
                if (s >= curStateCount || !delStates[s])
                {
                    int state = currentStates[s];
                    currentStates[d++] = state;
                    if ((stateMap[state][0] & LAZY) == LAZY && state > lazyAcceptor) 
                    {
                        backtrack = new StateSet ();
                        backtrack.emit(state, text.toString());
                        lazyAcceptor = state;
                    }
                }
            }
            stateCount = d;
            
            if (stateCount == 0)
                return UPDATE_NO_VALID_STATES;
            return UPDATE_NORMAL;
        }  
        public String statestr() 
        {
            StringBuilder r = new StringBuilder();
            if (accepted.length > 0)
                r.append (Arrays.toString(accepted)).append(' ');
            String s = "";
            for (int i = 0; i < stateCount; i++)
            {
                r.append(s).append(currentStates[i]);
                s = ", ";
            }
            if (backtrack != null) r.append ("<BT ").append(backtrack.statestr()).append('>');
            return r.toString();
        }
    }
    public interface CharSupplier
    {
        /** return the next character, or -1 to indicate completion */
        public int next() throws Exception;
    }   
    
    private final int[][] stateMap;
    private final int maxStates, initState;
    private StateSet current;
    private CharSupplier input;
    private Token lastAcceptedToken = Token.ERROR; 
    private String [] customCharSets = new String[32];
    private boolean debug;
    
    public NDFA(int[][] stateMap, int maxStates, int initState)
    {
        this.stateMap = stateMap;
        this.maxStates = maxStates;
        this.initState = initState;
        reset();
    }
    
    public void setCustomCharSet (int index, String chars)
    {
        customCharSets[index] = chars;
    }
    
    public void reset ()
    {
        current = new StateSet ();
    }
    
    public void setInput (CharSupplier input)
    {
        this.input = input;
    }
    
    public void setInput (final Reader r)
    {
        setInput (new CharSupplier() {
            public int next () throws Exception
            {
                return r.read();
            }
        });
    }
    
    public void setInput (final CharSequence cs)
    {
        setInput (new CharSupplier() {
            int i = 0;
            public int next()
            {
                if (i < cs.length())
                    return cs.charAt(i++);
                else
                    return -1;
            }
        });
    }
    public String statestr()
    {
        return current.statestr();
    }
    private void emit(StateSet ss)
    {
        lastAcceptedToken = ss.accepted[0];
        ss.clearFirst();
    }
    private boolean processPendingActions()
    {
        while (current.backtrackPending()) {
            current = current.backtrack;
            if (debug) System.out.println("backtrack: " + statestr());
        }
        if (current.emitPending())
        {
            emit (current);
            if (debug) System.out.println("emit pending: " + lastAcceptedToken);
 
            return true;
        }
        return false;
    }
    public int step()
    {
        if (processPendingActions()) return UPDATE_EAGER_ACCEPT;
        try
        {
            int ch = input.next();
            switch (current.update(ch))
            {
            case UPDATE_NORMAL:
                if (debug) System.out.println("read '"+(char)ch+"', states="+statestr());
                return UPDATE_NORMAL;

            case UPDATE_EAGER_ACCEPT:
                emit (current);
                if (debug) System.out.println("read '"+(char)ch+"', states="+statestr()+" -- ACCEPT "+lastAcceptedToken);
                return UPDATE_EAGER_ACCEPT;
                
            case UPDATE_NO_VALID_STATES:
                if (processPendingActions()) return UPDATE_EAGER_ACCEPT;
                if (current.canContinue())
                {
                    // should never get here, as should always emit while backtracking
                    return UPDATE_NORMAL;
                }
            default:
                if (debug) System.out.println("read '"+(char)ch+"', states="+statestr()+" -- ERROR");
                lastAcceptedToken = Token.ERROR;
                return UPDATE_ERROR;    
            }
        }
        catch (Exception e)
        {
            // FIXME report error
            lastAcceptedToken = Token.ERROR;
            return UPDATE_ERROR;
        } 
    }
    public Token nextToken()
    {
        while (step() == UPDATE_NORMAL)
            ;
            
        return lastAcceptedToken;
    }
    public boolean hasState(int state)
    {
        return current.hasState(state);
    }
    private final boolean classMatch (int classInclude, int classExclude, int ch)
    {
        return classMatch (classInclude, ch) && (classExclude == 0 || !classMatch (classExclude, ch));
    }
    private final boolean classMatch (int classId, int ch)
    {
        if (ch < 0) return false;
        if (classId >= CUSTOM) return customCharSets[classId - CUSTOM].indexOf((char)ch) >= 0;
        switch (classId)
        {
            case LETTER: return Character.isLetter(ch);   
            case DIGIT: return Character.isDigit(ch);
            case ANY: return true;
            case WS: return Character.isWhitespace(ch);
            default: return false;
        }
    }
    public void setDebug (boolean d) { debug = d; }
    public void showStateMap ()
    {
        for (int [] m : stateMap)
            System.out.println (Arrays.toString (m));
    }
}
