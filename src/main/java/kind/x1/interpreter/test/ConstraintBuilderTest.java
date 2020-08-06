package kind.x1.interpreter.test;

import kind.x1.*;
import kind.x1.ast.*;
import kind.x1.misc.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.types.Constraint;
import kind.x1.interpreter.types.ConstraintBuilder;

import java.util.List;
import java.util.Collections;

public class ConstraintBuilderTest extends Assertions implements Runnable
{
    public void run ()
    {
        simpleConstraint();        
    }
    
    public void simpleConstraint ()
    {
        ConstraintBuilder b = new ConstraintBuilder();
        b.relation (SID.from("test"));
        b.parameter (new Type.NamedType(SID.from("arg")));
        Constraint c = b.build();
        assertEqual ("simpleConstraint: relation", c.getRelation(), SID.from("test"));
        assertEqual ("simpleConstraint: parameters", c.getParameters().get(0).getName(), "arg");
    }
}
