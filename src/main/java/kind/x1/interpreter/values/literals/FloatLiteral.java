package kind.x1.interpreter.values.literals;

import java.util.Optional;
import kind.x1.interpreter.*;
import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.values.KVal;
import java.math.BigDecimal;

public class FloatLiteral implements KVal
{
    private final String sourceText;
    // FIXME hold flags
    
    public FloatLiteral (String text) { sourceText = text; }
    
    public Optional<Type> asType() { return Optional.empty(); }
    public Optional<Resolver> getStaticMemberResolver() { return Optional.empty(); }
    
    public String getSourceText () { return sourceText; }
    public BigDecimal getValue () { return new BigDecimal(sourceText.replaceAll("_","")); }
    
    // FIXME Kind-visible methods
}
