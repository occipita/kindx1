package kind.x1.interpreter.values;

import java.util.*;
import java.util.stream.*;
import kind.x1.interpreter.*;

public class FunctionBinder implements KCallable
{
    Optional<KVal> changeThisArg;
    Optional<List<KVal>> prependArgs;
    KCallable target;
    
    private FunctionBinder(Optional<KVal> thisArg, Optional<List<KVal>> args, KCallable target)
    {
	changeThisArg = thisArg;
	prependArgs = args;
	this.target = target;
    }
    public Continuation call (List<KVal> args, KVal thisArg, Resolver callingScope, ExecutionContext callingContext, BindableContinuation continuation)
    {
	return target.call (
	    prependArgs.map(pa -> joinLists(pa, args)).orElse(args),
	    changeThisArg.orElse(thisArg),
	    callingScope, callingContext, continuation);
    }

    public static FunctionBinder bindThisArg (KVal thisArg, KCallable target)
    {
	// FIXME collapse multiple binders
	return new FunctionBinder (Optional.of(thisArg), Optional.empty(), target);
    }

    private List<KVal> joinLists(List<KVal> a, List<KVal> b)
    {
	return Stream.concat (a.stream(), b.stream()).collect(Collectors.toList());
    }
}
