package kind.x1.interpreter.values.literals;

import java.util.Optional;
import kind.x1.interpreter.*;
import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.values.KVal;
import java.math.BigInteger;

public class IntLiteral implements KVal
{
    private final String sourceText;
    // FIXME hold flags
    
    public IntLiteral (String text) { sourceText = text; }
    
    public Optional<Type> asType() { return Optional.empty(); }
    public Optional<Resolver> getStaticMemberResolver() { return Optional.empty(); }
    
    public String getSourceText () { return sourceText; }
    public BigInteger getValue () { return new BigInteger(sourceText.replaceAll("_","")); }
    
    // FIXME Kind-visible methods
}
