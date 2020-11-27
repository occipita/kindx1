package kind.x1.interpreter.types;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;

import kind.x1.misc.SID;
import kind.x1.interpreter.*;
import kind.x1.interpreter.test.*;

public class FunctionTypeTest
{
    @Test
    public void nameOfTypeWithTypeParamsAndConstraints ()
    {
	TypeParameterContext tpc = new TypeParameterContext();
	Type t1 = tpc.addExplicit("T");
	tpc.addConstraint(new Constraint (SID.from("my::constraint"), Collections.singletonList(t1)));
	Type t2 = tpc.addExplicit("U");
	tpc.addConstraint(new Constraint (SID.from("my::otherConstraint"), Arrays.asList(t2, t1)));
	assertEquals (
	    "forall T, U : my::constraint(T), my::otherConstraint(U, T) . (T) -> U",
	    new FunctionType (
		Optional.of(tpc),
		Collections.singletonList(t1),
		Optional.of(t2),
		Optional.empty()).toString());

    }
    
}
