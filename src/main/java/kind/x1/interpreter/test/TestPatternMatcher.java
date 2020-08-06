package kind.x1.interpreter.test;

import kind.x1.interpreter.patterns.PatternMatcher;
import kind.x1.Optional;
import kind.x1.interpreter.types.Type;

public class TestPatternMatcher implements PatternMatcher
{
    public Optional<Type> getTypeRestriction() { return Optional.empty(); }    
}
