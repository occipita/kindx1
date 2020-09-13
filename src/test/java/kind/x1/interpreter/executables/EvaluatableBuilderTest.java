package kind.x1.interpreter.executables;

import kind.x1.interpreter.types.*;
import kind.x1.interpreter.types.primitive.*;
import kind.x1.interpreter.values.literals.*;
import org.junit.*;
import static org.junit.Assert.*;

public class EvaluatableBuilderTest
{
    @Test
    public void stringLiteral ()
    {
        EvaluatableBuilder b = new EvaluatableBuilder();
        b.stringLiteral("42");
        Evaluatable e = b.build();
        assertEquals ("type", e.getClass(), ConstVal.class);
        ConstVal cv = (ConstVal)e;
        assertEquals ("kind type", cv.getType(), LiteralTypes.STRINGLITERAL);
        assertEquals ("value", ((IntLiteral)cv.getValue()).getValue().longValue(), 42L);
    }
}

