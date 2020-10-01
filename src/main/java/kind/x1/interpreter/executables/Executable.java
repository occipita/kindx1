package kind.x1.interpreter.executables;

import kind.x1.interpreter.Resolver;
import kind.x1.interpreter.ExecutionContext;
import kind.x1.interpreter.Continuation;
import java.util.function.Consumer;

public interface Executable extends Continuation
{
    //override execute from Continuation to provide default
    default Continuation execute (Resolver resolver, ExecutionContext context, Continuation continuation) { return continuation; }

    public static final Executable NULL_EXECUTABLE= new Executable() {
	public String toString () { return "<null executable>"; }
    };
}
