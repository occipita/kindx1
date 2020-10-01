package kind.x1.interpreter;

public interface Continuation
{
    /**
     * Execute a small amount of work and return a continuation indicating the next
     * action to be performed.
     *@param resolver the Resolver for the current scope
     *@param context the ExecutionContext that provides continuations for exiting the current scope,
     *  function, chaining exceptions, etc.
     *@param continuation the default continuation (which moves on to the next statement or terminates
     *  the current function with no return value if this executable represents the last statement
     */
    default Continuation execute (Resolver resolver, ExecutionContext context, Continuation continuation) { return continuation; }


    /** Continuously execute continuations until a Continuation.EXIT is returned. */
    public static void executeUntilExit (Resolver resolver, ExecutionContext context, Continuation continuation)
    {
	
    }

    public static final Continuation EXIT = new Continuation () {
	public Continuation execute (Resolver resolver, ExecutionContext context, Continuation continuation) {
	    throw new RuntimeException("EXIT continuation executed");
	}
    };
    
}
