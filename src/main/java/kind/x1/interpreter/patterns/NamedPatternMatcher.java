package kind.x1.interpreter.patterns;

import java.util.Optional;
import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.symbols.*;
import kind.x1.interpreter.Scope;
import kind.x1.interpreter.values.KVal;

public class NamedPatternMatcher implements PatternMatcher
{
    private final String variableName;
    private final PatternMatcher filter;
    
    public NamedPatternMatcher (String vn, PatternMatcher f)
    {
        variableName = vn;
        filter = f;
    }
    
    public String getVariableName () { return variableName; }
    public PatternMatcher getFilter () { return filter; }
    
    public Optional<Type> getTypeRestriction() { 
        return filter.getTypeRestriction(); 
    }
    public void matchOrThrow (KVal value, Scope scope) throws PatternNotMatchedException
    {
	filter.matchOrThrow(value, scope);
	scope.addSymbol (new ConstSymbol (variableName, value, getTypeRestriction().orElse (Type.ANY)));
    }
    
}
