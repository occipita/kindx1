package kind.x1.interpreter.executables;

import java.util.*;
import java.util.concurrent.atomic.*;
import org.junit.*;
import static org.junit.Assert.*;

import kind.x1.interpreter.*;
import kind.x1.interpreter.values.*;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.test.*;

public class DotApplicationTest
{
    // FIXME import old type checker tests here
    
    @Test
    public void evaluatesCorrectly ()
    {
	TestKVal lhs = new TestKVal();
	Type memberType = new TestType("MemberType");
	TestKVal memberVal = new TestKVal();
	Type lhsType = new TestType("LHSType").addMember("testMember", memberType, val -> {
		assertEquals ("value passed to member fetch", val, lhs);
		return memberVal;
	});

	DotApplication sut = new DotApplication (new ConstVal(lhs, lhsType), "testMember");
	
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
	assertEquals ("binding result", memberVal, result.get());
    }

    @Test
    public void appliedToFunctionSetsThisArg ()
    {
	TestKVal lhs = new TestKVal();
	TestKVal expResult = new TestKVal();
	TestType lhsType = new TestType("LHSType");
	Type memberType = new FunctionType(Collections.emptyList(), Optional.of(new TestType("ResultType")), lhsType);
	KVal memberVal = new KCallable() {
	    public Continuation call (List<KVal> args, KVal thisArg, Resolver callingScope, ExecutionContext callingContext, BindableContinuation continuation)
	    {
		assertEquals ("this argument", lhs, thisArg);
		return continuation.bind(expResult);
	    }
	};
	lhsType.addMember("testMethod", memberType, val -> {
		assertEquals ("value passed to method fetch", val, lhs);
		return memberVal;
	});

	DotApplication sut = new DotApplication (new ConstVal(lhs, lhsType), "testMethod");
	
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
	((KCallable)result.get()).call (null, null, null, null, b);
	
	assertEquals ("method result", expResult, result.get());
    }

    
}
