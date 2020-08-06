package kind.x1.interpreter.symbols;

import java.util.List;
import java.util.ArrayList;
import kind.x1.interpreter.types.FunctionType;
import kind.x1.interpreter.types.Type;
import kind.x1.Optional;

public class OverloadedFunctionSymbol extends Symbol
{
    private List<FunctionSymbol> bodies;
    
    public OverloadedFunctionSymbol (String name, List<FunctionSymbol> bodies) { super(name); this.bodies = bodies; }
    public Optional<Type> getType () 
    {
        List<FunctionType> bts = new ArrayList<>();
        for (FunctionSymbol b : bodies) // let b hit the floor
            bts.add((FunctionType)b.getType().get());
        return Optional.of(new FunctionType(bts));
    }
}
