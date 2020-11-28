package kind.x1.interpreter.test;

import kind.x1.interpreter.types.*;
import java.util.*;


public class TestConstraintRelation extends ConstraintRelation
{
    public TestConstraintRelation (String name) { super (name); }

    public Optional<ConstraintEvidence> check(List<Type> types)
    {
	return Optional.of (new TestConstraintEvidence(this, types));
    }

    public String toString() { return "TestConstraintRelation("+getName()+")"; }
}
