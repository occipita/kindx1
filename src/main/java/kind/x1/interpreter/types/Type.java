package kind.x1.interpreter.types;

import java.util.Optional;
import kind.x1.interpreter.*;
import kind.x1.DiagnosticProducer;

public interface Type 
{
    String getName();
    boolean isFullyResolved();
    Optional<Type> resolve (Resolver r, DiagnosticProducer diag);
    boolean isSubTypeOf(Type t);

    public static Type ANY = new Type() {
	public String getName() { return "any"; }
	public boolean isFullyResolved() { return true; }
	public Optional<Type> resolve (Resolver r, DiagnosticProducer diag) { return Optional.of(this); }
	public boolean isSubTypeOf(Type t) { return t == ANY; }
    };
	
}
