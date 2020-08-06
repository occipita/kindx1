package kind.x1.interpreter.types;

import kind.x1.misc.SID;
import kind.x1.interpreter.values.KVal;
import kind.x1.*;
import kind.x1.interpreter.*;
import java.util.Optional;

/**
 * Defines a Kind type which is implemented via a Java class with 
 * annotations that define the types and names of methods, properties 
 * and operators that are exposed to Kind. Implementation methods are
 * passed and return raw KVal instances; no translation to Java types
 * is performed. This provides the simplest possible interface between
 * the two languages, allowing core Kind types to be implemented in
 * Java.
 */
public class AnnotatedJavaType implements Type, KVal
{
    private final SID canonicalName;
    private final Class<?> valueClass;
    
    // TODO extension classes
    // TODO interface implementations
    
    public AnnotatedJavaType (SID cname, Class<?> vclass) 
    { 
        canonicalName = cname;
        valueClass = vclass;
        // TODO build method, operator and property lists
    }
    
    public Optional<Type> asType() { return Optional.of(this); }
    public Optional<Resolver> getStaticMemberResolver() { return Optional.empty();/*FIXME*/ }
    
    public String getName () { return canonicalName.toString(); }
    public boolean isFullyResolved () { return true; }
    public Optional<Type> resolve (Resolver r, DiagnosticProducer diag) { return Optional.of(this); }    
    public boolean isSubTypeOf(Type t) { return false; /*FIXME*/ }

    public String toString() { return canonicalName.toString(); }
}
