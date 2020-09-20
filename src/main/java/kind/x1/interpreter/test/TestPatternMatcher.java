package kind.x1.interpreter.test;

import kind.x1.interpreter.patterns.PatternMatcher;
import java.util.Optional;
import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.Scope;
import kind.x1.interpreter.values.KVal;

public class TestPatternMatcher implements PatternMatcher
{
    public Optional<Type> getTypeRestriction() { return Optional.empty(); }
    public void matchOrThrow (KVal val, Scope s) { }
}
