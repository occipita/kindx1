package kind.x1.interpreter.test;

import kind.x1.Factory;
import kind.x1.interpreter.*;
import kind.x1.interpreter.executables.*;

public class TestEvaluatableBuilder extends EvaluatableBuilder
{
    Evaluatable test;
    
    public TestEvaluatableBuilder (Evaluatable t) { test = t; }
    
    public Evaluatable build() { return test; }
    
    public static Factory<EvaluatableBuilder> factoryReturning(final Evaluatable t)
    {
        return new Factory<EvaluatableBuilder>() {
            public EvaluatableBuilder create() { return new TestEvaluatableBuilder(t); }
        };
    }
}
