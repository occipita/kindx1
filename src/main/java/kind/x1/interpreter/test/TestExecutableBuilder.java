package kind.x1.interpreter.test;

import kind.x1.Factory;
import kind.x1.interpreter.*;
import kind.x1.interpreter.executables.*;

public class TestExecutableBuilder extends ExecutableBuilder
{
    Executable test;
    
    public TestExecutableBuilder (Executable t) { test = t; }
    
    public Executable build() { return test; }
    
    public static Factory<ExecutableBuilder> factoryReturning(final Executable t)
    {
        return new Factory<ExecutableBuilder>() {
            public ExecutableBuilder create() { return new TestExecutableBuilder(t); }
        };
    }
}
