package kind.x1.interpreter;

import kind.x1.interpreter.values.KVal;

/** 
 * Represents a continuation that must have a value bound to it 
 * before it can be executed.
 */
public interface BindableContinuation
{
    Continuation bind (KVal value);

    public static BindableContinuation discarding (Continuation next)
    {
	return discarded -> next;
    }
}
