package kind.x1.interpreter.test;

import kind.x1.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.symbols.ConstSymbol;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.types.primitive.*;
import kind.x1.interpreter.values.literals.*;
import kind.x1.misc.SID;
import java.util.Collections;

public class TypeSpecTest extends Assertions implements Runnable
{
    public void run ()
    {
        specificitySimple();
        // FIXME specificity with subtypes and supertypes
    }

    public void specificitySimple()
    {
        Type t1 = new TestType(SID.from("t1"));
        TypeSpec ext1 = TypeSpec.exactly(t1);
        TypeSpec subt1 = TypeSpec.subtypeOf(t1);
        TypeSpec supt1 = TypeSpec.supertypeOf(t1);
        
        assertFalse ("specificitySimple: UNSPECIFIED should not be more specific than " + ext1, TypeSpec.UNSPECIFIED.isMoreSpecificThan(ext1));
        assertFalse ("specificitySimple: UNSPECIFIED should not be more specific than " + subt1, TypeSpec.UNSPECIFIED.isMoreSpecificThan(subt1));
        assertFalse ("specificitySimple: UNSPECIFIED should not be more specific than " + supt1, TypeSpec.UNSPECIFIED.isMoreSpecificThan(supt1));
        
        assertTrue ("specificitySimple: " + ext1 + " should be more specific than UNSPECIFIED", ext1.isMoreSpecificThan(TypeSpec.UNSPECIFIED));
        assertTrue ("specificitySimple: " + subt1 + " should be more specific than UNSPECIFIED", subt1.isMoreSpecificThan(TypeSpec.UNSPECIFIED));
        assertTrue ("specificitySimple: " + supt1 + " should be more specific than UNSPECIFIED", supt1.isMoreSpecificThan(TypeSpec.UNSPECIFIED));        
    }
}
