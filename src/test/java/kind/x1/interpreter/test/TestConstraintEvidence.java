package kind.x1.interpreter.test;

import java.util.*;
import java.util.stream.*;
import kind.x1.interpreter.types.*;

public class TestConstraintEvidence implements ConstraintEvidence
{
    private ConstraintRelation relation;
    private List<Type> types;

    public TestConstraintEvidence (ConstraintRelation relation, List<Type> types)
    {
	this.relation = relation;
	this.types = types;
    }
    public String toString ()
    {
	return relation.toString() +
	    ".evidence" +
	    types.stream().map(Type::getName).collect(Collectors.joining(", ", "(", ")"));
    }
}
