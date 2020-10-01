package kind.x1.interpreter;

import java.util.Optional;
import java.util.Collections;
import java.util.Arrays;
import java.util.concurrent.atomic.*;

import org.junit.*;
import static org.junit.Assert.*;

public class ContinuationTest
{
    @Test(timeout=1000)
    public void canExitLoop ()
    {
	Continuation.executeUntilExit (null, null, (r, ec, c) -> Continuation.EXIT);
    }
    @Test(timeout=1000)
    public void firstContinuationIsCalled ()
    {
	AtomicBoolean called = new AtomicBoolean(false);
	Continuation.executeUntilExit (null, null, (r, ec, c) -> {
		called.set(true);
		return Continuation.EXIT;
	    });
	assertTrue ("Continuation should have been called", called.get());
    }
    @Test(timeout=1000)
    public void continuationsCalledRepeatedly ()
    {
	AtomicBoolean called = new AtomicBoolean(false);
	Continuation.executeUntilExit (null, null,
				       (r, ec, c) -> 
				       (r2, ec2, c2) -> {
					   called.set(true);
					   return Continuation.EXIT;
				       });
	assertTrue ("Continuation should have been called", called.get());
    }
}
