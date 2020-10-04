package kind.x1.interpreter;

import kind.x1.interpreter.values.KVal;

public class ExecutionContext
{
    public Continuation functionExit;
    public KVal returnValue;
}
