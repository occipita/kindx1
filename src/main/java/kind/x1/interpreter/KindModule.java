package kind.x1.interpreter;

import java.util.Optional;
import java.util.HashSet;
import java.util.ArrayList;
import kind.x1.interpreter.symbols.Symbol;

public class KindModule 
{
    private final Scope localScope = new Scope();
    private final HashSet<String> exports = new HashSet<>();
    
    public Scope getLocalScope() { return localScope; }
    public void export (String name) { exports.add(name); }
    public Optional<Symbol> getExportedSymbol(String id)
    {
        if (!exports.contains(id)) return Optional.empty();
        return localScope.getSymbol(id);
    }
    public Iterable<Symbol> getExportedSymbols ()
    {
        // FIXME this could be moreefficient
        ArrayList<Symbol> result = new ArrayList<>();
        for (Symbol s : localScope.getSymbols())
            if (exports.contains(s.getName())) result.add(s);
        return result;
    }
}
