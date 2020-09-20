package kind.x1.interpreter.patterns;

import kind.x1.interpreter.types.Type;
import java.util.Optional;
import kind.x1.interpreter.Scope;
import kind.x1.interpreter.values.KVal;

public class TypeCheckingPatternMatcher implements PatternMatcher
{
    private final Type type;
    
    public TypeCheckingPatternMatcher (Type t) { type = t; }
    
    public Type getType() { return type; }
    
    public Optional<Type> getTypeRestriction() { return Optional.of(type); }

    public void matchOrThrow (KVal val, Scope scope) { /* FIXME */ }
    
}
