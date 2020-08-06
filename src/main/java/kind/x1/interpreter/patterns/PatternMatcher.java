package kind.x1.interpreter.patterns;

import kind.x1.Optional;
import kind.x1.interpreter.types.Type;

public interface PatternMatcher 
{

    /** @return the narrowest known type that contains all values this matcher matches, if known. */ 
    Optional<Type> getTypeRestriction();

    public static final PatternMatcher ACCEPT = new PatternMatcher() {
        public Optional<Type> getTypeRestriction() { return Optional.empty(); }
    };
    public static final PatternMatcher REJECT = new PatternMatcher() {
        public Optional<Type> getTypeRestriction() { return Optional.empty(); }
    };
    
}
