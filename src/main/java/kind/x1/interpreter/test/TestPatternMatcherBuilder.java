package kind.x1.interpreter.test;

import kind.x1.Factory;
import kind.x1.interpreter.*;
import kind.x1.interpreter.patterns.*;

public class TestPatternMatcherBuilder extends PatternMatcherBuilder
{
    PatternMatcher test;
    
    public TestPatternMatcherBuilder (PatternMatcher t) { test = t; }
    
    public PatternMatcher build() { return test; }
    
    public static Factory<PatternMatcherBuilder> factoryReturning(final PatternMatcher t)
    {
        return new Factory<PatternMatcherBuilder>() {
            public PatternMatcherBuilder create() { return new TestPatternMatcherBuilder(t); }
        };
    }
}
