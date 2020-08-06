package kind.x1.interpreter.types;

import kind.x1.Optional;
import kind.x1.interpreter.*;
import kind.x1.DiagnosticProducer;

public interface Type 
{
    String getName();
    boolean isFullyResolved();
    Optional<Type> resolve (Resolver r, DiagnosticProducer diag);
    boolean isSubTypeOf(Type t);
}
