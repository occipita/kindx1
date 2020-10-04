package kind.x1.interpreter.executables;

import kind.x1.interpreter.*;
import kind.x1.interpreter.types.*;
import kind.x1.DiagnosticProducer;
import java.util.Optional;

public class TestEvaluatable implements Evaluatable
{
    public boolean inferTypesSilently (Resolver resolver, TypeSpec target) { return true; }
    public boolean inferTypes (Resolver resolver, TypeParameterContext context, DiagnosticProducer diag, TypeSpec target) { return true; }
    public boolean checkTypes (DiagnosticProducer diag) { return true; }
   

    public Optional<Type> getResultType () { return Optional.empty(); }
    
}
