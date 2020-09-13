
package kind.x1.interpreter.values.literals;

import java.util.Optional;
import kind.x1.interpreter.*;
import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.values.KVal;
import java.math.BigInteger;

public class StringLiteral implements KVal
{
    private final String sourceText;
    // FIXME hold flags
    
    public StringLiteral (String text) { sourceText = text; }
    
    public Optional<Type> asType() { return Optional.empty(); }
    public Optional<Resolver> getStaticMemberResolver() { return Optional.empty(); }
    
    public String getSourceText () { return sourceText; }
    public String getValue () { return sourceText.substring(1,sourceText.length()-1); }
    
    // FIXME Kind-visible methods
}
