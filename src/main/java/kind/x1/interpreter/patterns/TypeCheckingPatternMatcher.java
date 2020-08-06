package kind.x1.interpreter.patterns;

import kind.x1.interpreter.types.Type;
import kind.x1.Optional;

public class TypeCheckingPatternMatcher implements PatternMatcher
{
    private final Type type;
    
    public TypeCheckingPatternMatcher (Type t) { type = t; }
    
    public Type getType() { return type; }
    
    public Optional<Type> getTypeRestriction() { return Optional.of(type); }
    
}
