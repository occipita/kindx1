package kind.x1.interpreter.symbols;

import java.util.Optional;
import java.util.Collections;
import java.util.Arrays;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.patterns.*;
import kind.x1.interpreter.values.TestKVal;
import kind.x1.interpreter.Scope;
import org.junit.*;
import static org.junit.Assert.*;

public class FunctionSymbolTest
{
    @Test
    public void parameterScope () throws Exception
    {
	FunctionSymbol f = new FunctionSymbol("f", Optional.empty());
	TestKVal tv = new TestKVal(); 
	f.getParameters().add (new NamedPatternMatcher("p1", PatternMatcher.ACCEPT));
	Scope s = f.generateParameterScope (Arrays.asList(tv));
	assertEquals (Optional.of(tv), s.getSymbol("p1").map(sym -> ((ConstSymbol)sym).getValue()));
    }

    @Test(expected=PatternNotMatchedException.class)
    public void patternMatchFailure () throws Exception
    {
	FunctionSymbol f = new FunctionSymbol("f", Optional.empty());
	TestKVal tv = new TestKVal(); 
	f.getParameters().add (new NamedPatternMatcher("p1", PatternMatcher.REJECT));
	f.generateParameterScope (Arrays.asList(tv));
    }
}

