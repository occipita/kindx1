package kind.x1.interpreter.executables;

import java.util.concurrent.atomic.*;
import org.junit.*;
import static org.junit.Assert.*;

import kind.x1.interpreter.*;
import kind.x1.interpreter.values.*;

public class ReturnValueExecutableTest
{
    @Test
    public void returnsFunctionExitContinuation ()
    {
        Executable sut = new ReturnValueExecutable (new ConstVal(null, null));

	Continuation next = (r,ec,c1) -> null;
	ExecutionContext ec = new ExecutionContext ();
	ec.functionExit = next;
	Continuation r = sut.execute (null, ec, Continuation.EXIT);
	assertEquals ("execute() should have returned ec.functionExit", ec.functionExit, r);
    }
    @Test
    public void evaluatesAndStoresContents ()
    {
	AtomicReference<Continuation> c = new AtomicReference<>(null);
	TestKVal expectedResult = new TestKVal();
	Executable sut = new ReturnValueExecutable (new TestEvaluatable(){
	    public Continuation execute (Resolver resolver, ExecutionContext context, BindableContinuation continuation)
	    {
		c.set(continuation.bind(expectedResult));
		return c.get();
	    }
	});
	Continuation next = (r,ec,c1) -> null;
	ExecutionContext ec = new ExecutionContext ();
	ec.functionExit = next;
	Continuation r = sut.execute (null, ec, Continuation.EXIT);
	assertEquals ("execute() should have returned next", next, r);
	assertEquals ("next should have been returned after binding result", next, c.get());
	assertEquals ("function return value", expectedResult, ec.returnValue);
    }
}
