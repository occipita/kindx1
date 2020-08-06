package kind.x1.interpreter.symbols;

import java.util.Optional;
import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.values.KVal;

public class ConstSymbol extends Symbol
{
    private KVal value;
    private Type type;
    
    public ConstSymbol (String id, KVal v, Type t) { super(id); value = v; type = t; }
    
    public KVal getValue() { return value; }
    public Optional<Type> getType() { return Optional.of(type); }
}
