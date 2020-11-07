package kind.x1.interpreter.executables;

import java.util.*;
import java.util.concurrent.atomic.*;
import org.junit.*;
import static org.junit.Assert.*;

import kind.x1.interpreter.*;
import kind.x1.interpreter.values.*;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.test.*;

public class FunctionCallTest
{
    // FIXME import old type checker tests here
    
    @Test
    public void producesCorrectResultWithNoArgs ()
    {
	TestKVal expResult = new TestKVal();
	Type resultType = new TestType("ResultType");
	Type fnType = new FunctionType(Collections.emptyList(), Optional.of(resultType));
	KVal fnValue = new KCallable() {
	    public Continuation call (List<KVal> args, Resolver callingScope, ExecutionContext callingContext, BindableContinuation continuation)
	    {
		return continuation.bind(expResult);
	    }
	};

	FunctionCall sut = new FunctionCall (new ConstVal(fnValue, fnType), Collections.emptyList());
	
	Continuation next = (r,ec,c1) -> null;
	AtomicReference<KVal> result = new AtomicReference<>(null);
	
	BindableContinuation b = v -> {
	    result.set(v);
	    return next;
	};
	
	Continuation r = sut.execute (null, null, b);
	for (int limit = 0; limit < 10 && r != next && r != null; limit++)
	    r = r.execute (null, null, next);
	assertEquals ("execute() should have (eventually) returned next", next, r);
	assertEquals ("binding result", expResult, result.get());
    }
    @Test
    public void argsPassedCorrectly ()
    {
	TestKVal expResult = new TestKVal();
	Type resultType = new TestType("ResultType");
	TestKVal expArg1 = new TestKVal();
	Type arg1Type = new TestType("Arg1Type");
	TestKVal expArg2 = new TestKVal();
	Type arg2Type = new TestType("Arg2Type");
	Type fnType = new FunctionType(Arrays.asList(arg1Type, arg2Type), Optional.of(resultType));
	KVal fnValue = new KCallable() {
	    public Continuation call (List<KVal> args, Resolver callingScope, ExecutionContext callingContext, BindableContinuation continuation)
	    {
		assertEquals ("size of arg list in call method", 2, args.size());
		assertEquals ("arg 1 value", expArg1, args.get(0));
		assertEquals ("arg 2 value", expArg2, args.get(1));
		return continuation.bind(expResult);
	    }
	};

	FunctionCall sut = new FunctionCall (new ConstVal(fnValue, fnType), Arrays.asList(
						 new ConstVal(expArg1, arg1Type),
						 new ConstVal(expArg2, arg2Type)));
	
	Continuation next = (r,ec,c1) -> null;
	AtomicReference<KVal> result = new AtomicReference<>(null);
	
	BindableContinuation b = v -> {
	    result.set(v);
	    return next;
	};
	
	Continuation r = sut.execute (null, null, b);
	for (int limit = 0; limit < 10 && r != next && r != null; limit++)
	    r = r.execute (null, null, next);
	assertEquals ("execute() should have (eventually) returned next", next, r);
	assertEquals ("binding result", expResult, result.get());
    }
}
