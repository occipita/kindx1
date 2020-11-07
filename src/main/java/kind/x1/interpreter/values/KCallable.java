package kind.x1.interpreter.values;

import java.util.List;
import kind.x1.interpreter.*;

/** An interface for values that can be called as a function */
public interface KCallable extends KVal
{
    Continuation call (List<KVal> args, KVal thisArg, Resolver callingScope, ExecutionContext callingContext, BindableContinuation continuation);
}
