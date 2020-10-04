package kind.x1.interpreter.executables;

import kind.x1.interpreter.*;

public class EvaluatableExecutable implements Executable
{
    private Evaluatable evaluatable;
    
    public EvaluatableExecutable(Evaluatable e) { evaluatable = e; }
    public Evaluatable getEvaluatable() { return evaluatable; }
    public String toString () { return "<eval " + evaluatable + ">"; }

    public Continuation execute (Resolver resolver, ExecutionContext context, Continuation next)
    {
	return evaluatable.execute (resolver, context, BindableContinuation.discarding (next));
    }
}
