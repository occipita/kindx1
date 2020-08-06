package kind.x1.interpreter.types;

import kind.x1.*;
import kind.x1.interpreter.*;
import java.util.Optional;
import java.util.function.Function;

public class Ref implements Type
{
    private final Type target;
    
    public Ref(Type t) { target = t; }
    public Type getTarget() { return target; }
    
    public String getName() { return "ref("+target.getName()+")"; }
    public boolean isFullyResolved() { return target.isFullyResolved(); }
    public Optional<Type> resolve (Resolver r, DiagnosticProducer diag)
    {
        Optional<Type> t = target.resolve(r, diag);
        if (!t.isPresent()) return t;
        if (t.get() == target) return Optional.of(this);
        return Optional.of(new Ref (t.get()));
    }
    public boolean isSubTypeOf(Type t) { return false; /*FIXME*/ }
       
    public String toString () { return getName(); }
    public boolean equals(Object o)
    {
        if (o == null || !(o instanceof Ref)) return false;
        return ((Ref)o).target.equals(target);
    }
    public int hashCode () { return target.hashCode() ^ 1771; }
    
    public static Optional<Type> strip (Optional<Type> t)
    {
        if (!t.isPresent() || !(t.get() instanceof Ref)) return t;
        return Optional.of (((Ref)t.get()).target);
    }
    public static TypeSpec strip (TypeSpec t)
    {
        return t.mapType(STRIPPER);
    }
    public static Type strip (Type t)
    {
        if (t == null || !(t instanceof Ref)) return t;
        return ((Ref)t).target;
    }
    public static Function<Type,Type> STRIPPER = Ref::strip;
}
