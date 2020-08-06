package kind.x1.interpreter.symbols;

import kind.x1.ast.Defn;
import kind.x1.interpreter.executables.Executable;
import kind.x1.interpreter.types.Type;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class PropertySymbol extends Symbol
{
    private Optional<Type> type;
    private Map<Defn.AccessorType, Executable> accessors = new HashMap<>();
    
    public PropertySymbol (String n, Type t) { super (n); type= Optional.of(t); }
    void addAccessor (Defn.AccessorType atype, Executable e) { accessors.put(atype, e); }
    
    public Optional<Type> getType () { return type; } // FIXME should be LValue (if prop not readonly)!
    public Optional<Executable> getAccessor (Defn.AccessorType atype) { return Optional.ofNullable(accessors.get(atype)); }
}
