package kind.x1;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Stack;

public class MarkableTokenStream implements TokenStream
{
    TokenStream source;
    List<Token> buffering = null;
    Iterator<Token> replaying = null;
    Stack<Iterator<Token>> replayStack = new Stack<>();
    
    boolean debug;
    
    public MarkableTokenStream (TokenStream src) { source = src; }
    public Token nextToken()
    {
        Token t;
        if (replaying != null && replaying.hasNext()) {
            t = replaying.next();
            if (!replaying.hasNext() && !replayStack.isEmpty())    
                replaying = replayStack.pop();
        }
        else 
            t = source.nextToken();
            
        if (buffering != null) buffering.add(t);
        
        return t;
    }
    
    public void mark()
    {
        buffering = new ArrayList<>();
    }
    
    public void rewind ()
    {
        if (debug) System.out.println ("rewinding "+buffering.size()+" tokens");
        if (buffering.size() > 0)
        {
            if (replaying != null && replaying.hasNext())
                replayStack.push(replaying);
            replaying = buffering.iterator();
        }
        buffering = null;
    }
    
    public void release ()
    {
        buffering = null;
    }
}
