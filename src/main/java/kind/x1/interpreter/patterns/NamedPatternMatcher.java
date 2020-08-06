package kind.x1.interpreter.patterns;

import java.util.Optional;
import kind.x1.interpreter.types.Type;

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

}
