package kind.x1.interpreter.executables;

import kind.x1.interpreter.*;

public class ReturnExecutable implements Executable
{
    public Continuation execute (Resolver resolver, ExecutionContext context, Continuation continuation) { return context.functionExit; }

}
