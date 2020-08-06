package kind.x1.interpreter.types.primitive;

import kind.x1.interpreter.types.AnnotatedJavaType;
import kind.x1.interpreter.values.literals.*;
import kind.x1.misc.SID;

public abstract class LiteralTypes 
{
    public static final AnnotatedJavaType INTLITERAL = new AnnotatedJavaType (
        SID.from("kind::core::IntLiteral"),     // canonical name
        IntLiteral.class);                      // main implementation class
}
