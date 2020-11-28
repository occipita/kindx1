package kind.x1.interpreter.executables;

import java.util.*;
import java.util.concurrent.atomic.*;
import org.junit.*;
import static org.junit.Assert.*;

import kind.x1.interpreter.*;
import kind.x1.interpreter.values.*;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.test.*;
import kind.x1.misc.SID;

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
	    public Continuation call (List<KVal> args, KVal thisArg, List<ConstraintEvidence> evidence,
				      Resolver callingScope, ExecutionContext callingContext, BindableContinuation continuation)
	    {
		return continuation.bind(expResult);
	    }
	};

	assertEquals ("binding result", expResult, testExec(new FunctionCall (new ConstVal(fnValue, fnType), Collections.emptyList())));
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
	    public Continuation call (List<KVal> args, KVal thisArg, List<ConstraintEvidence> evidence,
				      Resolver callingScope, ExecutionContext callingContext, BindableContinuation continuation)
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
	
	assertEquals ("binding result", expResult, testExec(sut));
    }

    @Test
    public void constraintEvidenceSuccessfullyPassed ()
    {
	TestKVal expResult = new TestKVal();
	Type resultType = new TestType("ResultType");
	TestKVal expArg1 = new TestKVal();
	Type arg1Type = new TestType("Arg1Type");
	TestKVal expArg2 = new TestKVal();
	Type arg2Type = new TestType("Arg2Type");
	TypeParameterContext tpc = new TypeParameterContext ();
	Type arg1TP = tpc.addImplicit();
	Type arg2TP = tpc.addImplicit();
	Constraint c1 = new Constraint(SID.from("constraint1"), Collections.singletonList(arg1TP));
	Constraint c2 = new Constraint(SID.from("constraint2"), Collections.singletonList(arg2TP));
	tpc.addConstraint (c1);
	tpc.addConstraint (c2);
	Scope s = new Scope ();
	s.addSymbol (new TestConstraintRelation ("constraint1"));
	s.addSymbol (new TestConstraintRelation ("constraint2"));
	Resolver res = Resolver.newScope (Resolver.EMPTY, s);
	assertTrue ("TypeParameterContext resolution should succeed",
		    tpc.resolve (res));
	
	Type fnType = new FunctionType(Optional.of(tpc), Arrays.asList(arg1Type, arg2Type), Optional.of(resultType), Optional.empty());
	KVal fnValue = new KCallable() {
	    public Continuation call (List<KVal> args, KVal thisArg, List<ConstraintEvidence> evidence,
				      Resolver callingScope, ExecutionContext callingContext, BindableContinuation continuation)
	    {
		assertEquals ("size of evidence list in call method", 2, evidence.size());
		assertEquals ("evidence[0]", "TestConstraintRelation(constraint1).evidence(Arg1Type)", evidence.get(0).toString());
		assertEquals ("evidence[1]", "TestConstraintRelation(constraint2).evidence(Arg2Type)", evidence.get(1).toString());
		return continuation.bind(expResult);
	    }
	};

	FunctionCall sut = new FunctionCall (new ConstVal(fnValue, fnType), Arrays.asList(
						 new ConstVal(expArg1, arg1Type),
						 new ConstVal(expArg2, arg2Type)));

	// must be fully type-checked for evidence list to be correct
	TypeSpec rtspec=TypeSpec.subtypeOf(resultType);
	TestDiagnosticProducer diag = new TestDiagnosticProducer();
	sut.inferTypesSilently (res, rtspec);
	sut.inferTypes (res, null, diag, rtspec);
	sut.checkTypes (diag);

	assertEquals ("binding result", expResult, testExec(sut));
	
    }

    public KVal testExec (Evaluatable sut)
    {
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
	return result.get();
    }
}
