package kind.x1.interpreter.executables;

import kind.x1.misc.SID;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.values.KVal;
import kind.x1.interpreter.symbols.Symbol;
import kind.x1.interpreter.*;
import kind.x1.*;

public class VariableRef implements Evaluatable
{
    private SID id;
    private Optional<Type> type = Optional.empty();
    private Optional<Symbol> symbol = Optional.empty();
    public VariableRef (SID id) { this.id = id; }
    public SID getId () { return id; }    
    
    public boolean inferTypesSilently (Resolver resolver, TypeSpec target) 
    {
        if (!symbol.isPresent()) symbol = resolver.resolve(id);
        if (symbol.isPresent() && !type.isPresent()) type = symbol.get().getType();
        return type.isPresent(); 
    }
    public boolean inferTypes (Resolver resolver, TypeParameterContext context, DiagnosticProducer diag, TypeSpec expected) 
    {
        if (!inferTypesSilently(resolver, expected))
        {
            diag.error("Could not resolve variable '" + id +"'");
            return false;
        } 
        return true; 
    }
    public boolean checkTypes (DiagnosticProducer diag) { return true; }

    public Optional<Type> getResultType () { return type; }
}
