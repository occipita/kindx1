package kind.x1.interpreter;

import java.util.TreeMap;
import java.util.Map;
import kind.x1.Optional;
import kind.x1.interpreter.symbols.Symbol;

public class Scope 
{
    private Map<String,Symbol> symbols = new TreeMap<>();
    
    public void addSymbol (Symbol s) {
        Symbol old = symbols.get(s.getName()); 
        if (old != null) s = s.mergeWith(old);
        symbols.put (s.getName(), s); 
    }
    public Optional<Symbol> getSymbol (String name) { return Optional.ofNullable(symbols.get(name)); }
    public Iterable<Symbol> getSymbols() { return symbols.values(); }
}
