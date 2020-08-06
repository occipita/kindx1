package kind.x1.interpreter.test;

import kind.x1.*;
import kind.x1.misc.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.types.*;

import java.util.List;
import java.util.Collections;

public class TypeBuilderTest extends Assertions implements Runnable
{
    public void run()
    {
        namedType();
        functionType();
        noreturnFunctionType();
        voidType();
    }

    public void namedType ()
    {
        TypeBuilder b =new TypeBuilder();
        b.namedType (SID.from("type::name"));
        Type t = b.build();
        assertEqual ("namedType: result class", t.getClass(), TypeReference.class);
        assertFalse ("namedType: result should not be fully resolved", t.isFullyResolved());
        assertEqual ("namedType: name", t.getName(), "type::name");
    }

    // FIXME don't actually have a syntax for this yet, butneed the types for    inference#
    public void functionType ()
    {
        TypeBuilder b = new TypeBuilder();
        b.beginFunction();
        b.visitFunctionParameter().namedType(SID.from("int"));
        b.visitFunctionParameter().namedType(SID.from("double"));
        b.visitFunctionReturnType().namedType(SID.from("complex"));
        b.endFunction();
        Type t = b.build();
        assertEqual ("functionType: result class", t.getClass(), FunctionType.class);
        assertFalse ("functionType: result should not be fully resolved", t.isFullyResolved());
        FunctionType ft = (FunctionType)t;
        assertEqual ("functionType: param 0", ft.getElements().get(0).getParameters().get(0).getName(), "int");
        assertEqual ("functionType: param 1", ft.getElements().get(0).getParameters().get(1).getName(), "double");
        assertEqual ("functionType: return type", ft.getElements().get(0).getReturnType().get().getName(), "complex");
        assertEqual ("functionType: name", ft.getName(), "(int, double) -> complex");        
    }
    public void noreturnFunctionType ()
    {
        TypeBuilder b = new TypeBuilder();
        b.beginFunction();
        b.endFunction();
        Type t = b.build();
        assertEqual ("noreturnFunctionType: result class", t.getClass(), FunctionType.class);
        assertTrue ("noreturnFunctionType: result should be fully resolved", t.isFullyResolved());
        FunctionType ft = (FunctionType)t;
        assertFalse ("noreturnFunctionType: return type should be empty", ft.getElements().get(0).getReturnType().isPresent());
        assertEqual ("noreturnFunctionType: name", ft.getName(), "() -> noreturn");        
    }
    public void voidType ()
    {
        TypeBuilder b =new TypeBuilder();
        b.voidType();
        Type t = b.build();
        assertEqual ("voidType: result class", t.getClass(), VoidType.class);
        assertTrue ("voidType: result should be fully resolved", t.isFullyResolved());
        assertEqual ("voidType: name", t.getName(), "void");
    }
}
