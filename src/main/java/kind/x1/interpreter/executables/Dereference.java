package kind.x1.interpreter.executables;

import kind.x1.interpreter.*;
import kind.x1.*;
import kind.x1.interpreter.types.*;

public class Dereference implements Evaluatable
{
    public final Evaluatable subExpr;
    
    public Dereference(Evaluatable sub) { subExpr = sub; }
    
    public boolean inferTypesSilently (Resolver resolver, TypeSpec target) 
    { 
        return subExpr.inferTypesSilently(resolver, Ref.strip(target));
    }
    public boolean inferTypes (Resolver resolver, TypeParameterContext context, DiagnosticProducer diag, TypeSpec target)
    {
        return subExpr.inferTypes (resolver, context, diag, Ref.strip(target));
    }
    public boolean checkTypes (DiagnosticProducer diag)
    {
        if (!subExpr.checkTypes(diag)) return false;
        Optional<Type> r = subExpr.getResultType();
        if (!r.isPresent() || !(r.get() instanceof Ref))
        {
            diag.error ("Dereference can only be applied to a reference");
            return false;
        }
        return true;
    }
    public Optional<Type> getResultType () { return subExpr.getResultType().map(Ref.STRIPPER); }
    
}
