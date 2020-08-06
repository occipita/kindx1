package kind.x1.interpreter.symbols;

import kind.x1.Optional;
import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.values.KVal;

public abstract class Symbol 
{
    private final String name;

    public Symbol (String name) { this.name = name; }
    
    public String getName () { return name; }
    public KVal getValue () { throw new RuntimeException ("Symbol type not implemented"); }
    public Optional<Type> getType () { throw new RuntimeException ("Symbol type not implemented"); }
    
    //public void resolveType(Resolver r, DiagnosticProducer p, TypeParameterContext tpc) { }
    public Symbol mergeWith (Symbol oldSymbol) { return this; }
}
