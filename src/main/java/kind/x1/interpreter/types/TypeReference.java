package kind.x1.interpreter.types;

import kind.x1.misc.SID;
import kind.x1.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.symbols.*;

public class TypeReference implements Type 
{
    private final SID sid;
    
    public TypeReference (SID s) { sid = s; }
    public String getName() { return sid.toString(); }
    public boolean isFullyResolved () { return false; }   
    public Optional<Type> resolve (Resolver r, DiagnosticProducer diag) 
    {
        Optional<Symbol> s = r.resolve(sid);
        if (!s.isPresent()) {
            diag.error ("Could not resolve type '" + sid + "'");
            return Optional.empty();
        }
        Optional<Type> result = s.get().getValue().asType();
        if (!result.isPresent()) diag.error ("Object '"+sid+"' is not a type");
        return result; 
    }    
    public boolean isSubTypeOf(Type t) { return false; /*FIXME*/ }
    
    public boolean equals (Object o)
    {
        if (o.getClass() != getClass())return false;
        TypeReference other=(TypeReference) o;
        return sid.equals(other.sid);
    }
    public int hashCode () { return sid.hashCode(); }
    public String toString() { return "TypeReference<"+sid+">"; }
    
}
