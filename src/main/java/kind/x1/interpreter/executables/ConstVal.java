package kind.x1.interpreter.executables;

import kind.x1.interpreter.types.*;
import kind.x1.interpreter.values.KVal;
import kind.x1.interpreter.*;
import kind.x1.*;
import java.util.Optional;

public class ConstVal implements Evaluatable
{
    private final KVal value;
    private Type type;
    
    public ConstVal (KVal v, Type t) { value = v; type = t; }
    public KVal getValue() { return value; }
    public Type getType () { return type; }
    
    public boolean inferTypesSilently (Resolver resolver, TypeSpec target) { return true; }
    public boolean inferTypes (Resolver resolver, TypeParameterContext context, DiagnosticProducer diag, TypeSpec expected) 
    {
        if (!type.isFullyResolved())
        {
            Optional<Type> t = type.resolve(resolver, diag);
            if (t.isPresent()) type = t.get();
        } 
        if (expected != TypeSpec.UNSPECIFIED && expected.getType() instanceof TypeParameterContext.Parameter && !(type instanceof TypeParameterContext.Parameter))
        {
            ((TypeParameterContext.Parameter)expected.getType()).unifyWith  (type);
            return true;
        }
        return true; // expected.matches(type); ???
    }
    public boolean checkTypes (DiagnosticProducer diag) { return type.isFullyResolved(); }
    
    public Optional<Type> getResultType () { return Optional.of(type); }
    
    
}
