package kind.x1.interpreter.symbols;

import kind.x1.interpreter.types.*;
import kind.x1.interpreter.patterns.PatternMatcher;
import kind.x1.interpreter.executables.Executable;
import kind.x1.Optional;
import kind.x1.interpreter.TypeParameterContext;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


public class FunctionSymbol extends Symbol
{
    private Optional<TypeParameterContext> typeParameterContext;
    private Type returnType;
    private List<PatternMatcher> parameters = new ArrayList<>();
    private Optional<Type> type = Optional.empty();;
    
    private Optional<Executable> executable = Optional.empty();
    private boolean isAbstract;
    
    public FunctionSymbol (String name, Optional<TypeParameterContext> tpc) 
    { 
        super(name);
        typeParameterContext = tpc; 
    }
    public boolean isAbstract() { return isAbstract; }
    public void setAbstract (boolean v) { isAbstract = v; }
    public Type getReturnType () { return returnType; }
    public void setReturnType (Type t) { returnType = t; }
    public List<PatternMatcher> getParameters() { return parameters; }
    public Optional<Executable> getExecutable() { return executable; }
    public void setExecutable(Optional<Executable> e) { executable = e; }
    
    public Optional<Type> getType ()
    {
        if (!type.isPresent())
        {
            List<Type> pt = new ArrayList<>();
            for (PatternMatcher p : parameters)
            {
                Optional<Type> restriction = p.getTypeRestriction();
                if (restriction.isPresent())
                    pt.add (restriction.get());
                else
                {
                    if (!typeParameterContext.isPresent()) typeParameterContext = Optional.of(new TypeParameterContext());
                    pt.add (typeParameterContext.get().addImplicit());    
                }
                
            }
            if (typeParameterContext.isPresent())
                type = Optional.of(typeParameterContext.get().getConstructor(new FunctionType(pt, Optional.of(returnType))));
            else
                type = Optional.of(new FunctionType(pt, Optional.of(returnType)));
        }
        return type;
    }
    
    public Symbol mergeWith (Symbol old)
    {
        if (old instanceof FunctionSymbol) 
            return new OverloadedFunctionSymbol (getName(), Arrays.asList ((FunctionSymbol)old, this));
        return this;
    }
}
