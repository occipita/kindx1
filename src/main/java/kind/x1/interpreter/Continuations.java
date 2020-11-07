package kind.x1.interpreter;

import java.util.*;
import java.util.function.*;
import kind.x1.interpreter.executables.*;
import kind.x1.interpreter.values.KVal;

public abstract class Continuations
{
    /**
     * Evaluate a sequence of evaluatables and collect the results in a list, finally passing the
     * collected results to a function that produces a continuation to consume it
     */
    public static Continuation mapList (Iterable<Evaluatable> source, Resolver resolver, ExecutionContext context,
				 Function<List<KVal>, Continuation> receiver)
    {
	return new ContinuationMapper (source.iterator(), new ArrayList<KVal>(), resolver, context, receiver).next();
    }

    private static class ContinuationMapper
    {
	Iterator<Evaluatable> toEvaluate;
	ArrayList<KVal> values;
	BindableContinuation valueReceiver;
	Resolver resolver;
	ExecutionContext context;
	Function<List<KVal>, Continuation> receiver;
	
	ContinuationMapper (Iterator<Evaluatable> toEvaluate, ArrayList<KVal> values,
			    Resolver resolver, ExecutionContext context, Function<List<KVal>, Continuation> receiver)
	{
	    this.toEvaluate = toEvaluate;
	    this.values = values;
	    this.resolver = resolver;
	    this.context = context;
	    this.receiver = receiver;
	    
	    valueReceiver = value -> {
		values.add(value);
		return next();
	    };
	};

	Continuation next ()
	{
	    if (toEvaluate.hasNext())
		return toEvaluate.next().execute (resolver, context, valueReceiver);
	    else
		return receiver.apply (values);
	}
    }
}
