package kind.x1.interpreter.types.primitive;

import kind.x1.interpreter.types.*;
import kind.x1.interpreter.values.literals.*;
import kind.x1.interpreter.values.*;
import kind.x1.misc.SID;

public abstract class CoreTypes 
{
    public static final AnnotatedJavaType ANNOTATEDJAVATYPE = new AnnotatedJavaType (
        SID.from("kind::x1::AnnotatedJavaType"),       // canonical name (in x1 as this type is an implementation detail)
        AnnotatedJavaType.class);                      // main implementation class
    public static final AnnotatedJavaType BOOLEAN = new AnnotatedJavaType (
        SID.from("kind::x1::boolean"),                 // canonical name (in x1 as this type is an implementation detail)
        KBoolean.class);                               // main implementation class

}
