package kind.x1.interpreter.patterns;

import java.util.Optional;
import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.values.KVal;
import kind.x1.interpreter.Scope;
import kind.x1.misc.Lazy;

public interface PatternMatcher 
{

    /** @return the narrowest known type that contains all values this matcher matches, if known. */ 
    Optional<Type> getTypeRestriction();

    /** Attempt to match a value against the pattern, adding new variables to a given scope as required.
     *@throws PatternNotMatchedException if the pattern does not match. This exception is cached, so its stack
     * trace may be inaccurate,
     */
    void matchOrThrow(KVal value, Scope scope) throws PatternNotMatchedException;
    
    public static final PatternMatcher ACCEPT = new PatternMatcher() {
        public Optional<Type> getTypeRestriction() { return Optional.empty(); }
	public void matchOrThrow (KVal value, Scope scope) { }
    };
    public static final PatternMatcher REJECT = new PatternMatcher() {
	private Lazy<PatternNotMatchedException> exception = new Lazy<>(() -> new PatternNotMatchedException(this));
        public Optional<Type> getTypeRestriction() { return Optional.empty(); }
	public void matchOrThrow (KVal value, Scope scope) throws PatternNotMatchedException { throw exception.get(); }
    };
    
}
