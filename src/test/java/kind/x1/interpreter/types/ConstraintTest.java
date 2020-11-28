package kind.x1.interpreter.types;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

import kind.x1.misc.SID;
import kind.x1.interpreter.*;
import kind.x1.interpreter.test.*;

public class ConstraintTest
{
    @Test
    public void canResolveAndCheckRelations ()
    {
	TypeParameterContext tpc = new TypeParameterContext();
	Type param = tpc.addExplicit("P");
	Type other = new TestType("Other");
	Type real = new TestType("Real");
	Constraint c = new Constraint (SID.from("relation"), Arrays.asList(param, other));
	Scope s = new Scope ();

	s.addSymbol (new TestConstraintRelation("relation"));

	assertTrue ("constraint should resolve", c.resolve (Resolver.newScope(Resolver.EMPTY, s)));
	assertEquals ("evidence should be returned",
		      "TestConstraintRelation(relation).evidence(Real, Other)",
		      c.checkSatisfied (Collections.singletonMap(param, real)).map(Object::toString).orElse("none"));
		     
    }
	
}
