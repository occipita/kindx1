package kind.x1.interpreter.values;

import java.util.List;
import kind.x1.interpreter.*;
import kind.x1.interpreter.types.ConstraintEvidence;

/** An interface for values that can be called as a function */
public interface KCallable extends KVal
{
    /**
     * Calls the function represented by this object.
     * @param args     arguments to be passed to the function, which must agree with the type and number of arguments expected 
     *                 by the function's type
     * @param thisArg  the value of the object on which this method is being called (if specified)
     * @param evidence evidence that the type constraints in the function's type are satisfied
     * @param callingScope  the @{link Resolver} of the calling scope
     * @param callingContext the execution context at the point of the call
     * @param continuation a @{link BindableContinuation} that will receive the result of this function and return the 
     *                 continuation at which execution will resume once the function returns
     * @returns a continuation that will eventually resolve to either the continuation returned by calling <code>continuation.bind(result)</code>
     *          or one or the special continuations contained in <code>callingContext</code>.
     */
    Continuation call (List<KVal> args, KVal thisArg, List<ConstraintEvidence> evidence, Resolver callingScope, ExecutionContext callingContext, BindableContinuation continuation);
}
