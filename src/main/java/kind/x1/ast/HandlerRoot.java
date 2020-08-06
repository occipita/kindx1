package kind.x1.ast;

import kind.x1.*;
import java.util.*;
import kind.x1.misc.*;

public class HandlerRoot 
{
    private Expr.Handler expr = new Expr.Handler();
    private Stmt.Handler stmt = new Stmt.Handler();
    private Type.Handler type = new Type.Handler();
    private Pattern.Handler pattern = new Pattern.Handler();
    private Defn.Handler defn = new Defn.Handler();
    private Mod.Handler mod = new Mod.Handler();
  
    public Expr.Handler expr () { return expr; }
    public Stmt.Handler stmt () { return stmt; }
    public Type.Handler type () { return type; }
    public Pattern.Handler pattern () { return pattern; }
    public Defn.Handler defn() { return defn; }
    public Mod.Handler mod() { return mod; }
      
    public <T> T copy (T in) { return in; }
    public Token mergeId (Token start,Token end) { return new Token (TokenType.ID, start.text()+end.text()); }
    public <T> LinkedList<T> emptyLinkedList() { return new LinkedList<T>(); }
    public <T> LinkedList<T> prepend (T val, LinkedList<T> lst) {
        lst.addFirst(val);
        return lst;
    }
    public <T> LinkedList<T> linkedListTail (T val) {
        return prepend(val,emptyLinkedList());
    }
    public List<String> tokenListToStrings (List<Token> tokens) {
        ArrayList<String> result = new ArrayList<>(tokens.size());
        for (Token t : tokens)
            result.add(t.text());
        return result;
    }
    public <T1,T2> Pair<T1,T2> pair (T1 a, T2 b) { return new Pair<>(a,b); }
    public SID sid (List<Token> t) { return new SID(tokenListToStrings(t)); }
            
}
