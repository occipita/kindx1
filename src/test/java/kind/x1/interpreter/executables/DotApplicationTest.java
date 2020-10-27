package kind.x1.interpreter.executables;

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
	for (int limit = 0; limit < 10 && r != next && r != null; )
	    r = r.execute (null, null, next);
	assertEquals ("execute() should have (eventually) returned next", next, r);
	assertEquals ("binding result", memberVal, result.get());
    }
}
