package kind.x1.interpreter.test;

import kind.x1.Factory;
import kind.x1.interpreter.*;
import kind.x1.interpreter.symbols.*;

public class TestSymbolBuilder extends SymbolBuilder
{
    Symbol test;
    
    public TestSymbolBuilder (Symbol t) { test = t; }
    
    public Symbol build() { return test; }
    
    public static Factory<SymbolBuilder> factoryReturning(final Symbol t)
    {
        return new Factory<SymbolBuilder>() {
            public SymbolBuilder create() { return new TestSymbolBuilder(t); }
        };
    } 
}
