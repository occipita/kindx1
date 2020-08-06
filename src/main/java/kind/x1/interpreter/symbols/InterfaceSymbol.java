package kind.x1.interpreter.symbols;

import kind.x1.interpreter.types.*;

import java.util.List;
import java.util.ArrayList;

public class InterfaceSymbol  extends Symbol
{
    private List<String> parameters = new ArrayList<>();
    private List<Constraint> parameterConstraints = new ArrayList<>();
    private List<Type> superinterfaces = new ArrayList<>();
    private List<String> placeholders = new ArrayList<>();
    private List<Symbol> entries = new ArrayList<>();
    
    public InterfaceSymbol(String name) { super(name); }
    public List<String> getParameters () { return parameters; } 
    public List<Constraint> getParameterConstraints () { return parameterConstraints; } 
    public List<Type> getSuperinterfaces () { return superinterfaces; }
    public List<String> getPlaceholders () { return placeholders; }
    public List<Symbol> getEntries () { return entries; }
}
