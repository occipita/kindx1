package kind.x1.interpreter.executables;

import java.util.concurrent.atomic.*;
import org.junit.*;
import static org.junit.Assert.*;

import kind.x1.interpreter.*;
import kind.x1.interpreter.values.*;

public class ReturnExecutableTest
{
    @Test
    public void returnsFunctionExitContinuation ()
    {
	Executable sut = new ReturnExecutable ();

	Continuation next = (r,ec,c1) -> null;
	ExecutionContext ec = new ExecutionContext ();
	ec.functionExit = next;
	Continuation r = sut.execute (null, ec, Continuation.EXIT);
	assertEquals ("execute() should have returned ec.functionExit", ec.functionExit, r);
    }
}
