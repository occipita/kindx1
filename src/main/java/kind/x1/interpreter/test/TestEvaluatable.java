package kind.x1.interpreter.test;

import kind.x1.interpreter.types.*;
import kind.x1.interpreter.values.KVal;
import kind.x1.interpreter.*;
import kind.x1.*;

public class TestEvaluatable implements kind.x1.interpreter.executables.Evaluatable
{
    public boolean inferTypesSilently (Resolver resolver, TypeSpec target) { return false; }
    public boolean inferTypes (Resolver resolver, TypeParameterContext context, DiagnosticProducer diag, TypeSpec expected) { return true; }
    public boolean checkTypes (DiagnosticProducer diag) { return false; }

    public Optional<Type> getResultType () { return Optional.empty(); }

}
