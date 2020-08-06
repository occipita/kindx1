package kind.x1.interpreter.types;

import kind.x1.misc.SID;
import kind.x1.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.symbols.*;
import java.util.Optional;

public class VoidType implements Type 
{
    public String getName() { return "void"; }
    public boolean isFullyResolved () { return true; }   
    public Optional<Type> resolve (Resolver r, DiagnosticProducer diag) 
    {
        return Optional.of(this);
    }    
    public boolean isSubTypeOf(Type t) { return false; /*FIXME*/ }
    
    public boolean equals (Object o)
    {
        if (o.getClass() != getClass()) return false;
        return true;
    }
    public int hashCode () { return 1; }
    public String toString() { return "VoidType"; }
    
}
