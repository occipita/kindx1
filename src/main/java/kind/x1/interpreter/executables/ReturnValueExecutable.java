package kind.x1.interpreter.executables;

import kind.x1.interpreter.*;

public class ReturnValueExecutable implements Executable
{
    private Evaluatable evaluatable;
    
    public ReturnValueExecutable(Evaluatable e) { evaluatable = e; }
    public Evaluatable getEvaluatable() { return evaluatable; }
    public String toString () { return "<ret " + evaluatable + ">"; }
    public Continuation execute (Resolver resolver, ExecutionContext context, Continuation next)
    {
	return evaluatable.execute (resolver, context, value -> {
		context.returnValue = value;
		return context.functionExit;
	    });
    }
}
