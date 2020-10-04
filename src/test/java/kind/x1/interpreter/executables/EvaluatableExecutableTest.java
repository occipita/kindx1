package kind.x1.interpreter.executables;

import java.util.concurrent.atomic.*;
import org.junit.*;
import static org.junit.Assert.*;

import kind.x1.interpreter.*;
import kind.x1.interpreter.values.*;

public class EvaluatableExecutableTest
{
    @Test
    public void evaluatesAndIgnoresContents ()
    {
	AtomicReference<Continuation> c = new AtomicReference<>(null);
	EvaluatableExecutable sut = new EvaluatableExecutable (new TestEvaluatable(){
	    public Continuation execute (Resolver resolver, ExecutionContext context, BindableContinuation continuation)
	    {
		c.set(continuation.bind(new TestKVal()));
		return c.get();
	    }
	});
	Continuation next = (r,ec,c1) -> null;
	Continuation r = sut.execute (null, null, next);
	assertEquals ("execute() should have returned next", next, r);
	assertEquals ("next should have been returned after binding result", next, c.get());
    }
}
