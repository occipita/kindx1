package kind.x1.interpreter.test;

import kind.x1.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.symbols.ConstSymbol;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.types.primitive.*;
import kind.x1.interpreter.values.literals.*;
import kind.x1.misc.SID;
import java.util.Collections;
import java.util.Optional;

public class ResolutionTest extends Assertions implements Runnable
{
    public void run ()
    {
        typeResolutionDefault();
        typeReference();
        functionType();
    }

    public void typeResolutionDefault ()
    {
        assertEqual ("typeResolutionDefault: resolved type", 
            LiteralTypes.INTLITERAL.resolve (Resolver.EMPTY, new TestDiagnosticProducer()),
            Optional.of(LiteralTypes.INTLITERAL));
    }
        
    public void typeReference ()
    {
        Scope s = new Scope();
        s.addSymbol (new ConstSymbol("IntLiteral", LiteralTypes.INTLITERAL, CoreTypes.ANNOTATEDJAVATYPE));
        s.addSymbol (new ConstSymbol("value", new IntLiteral("42"), LiteralTypes.INTLITERAL));
        Resolver r = Resolver.newScope (Resolver.EMPTY, s);
        Type ref = new TypeReference(SID.from("IntLiteral"));
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        Optional<Type> t = ref.resolve (Resolver.EMPTY, diag);
        assertEqual ("typeReference: resolution failure", t, Optional.empty());
        assertEqual ("typeReference: resolution failure error count", diag.getErrors().size(), 1);
        assertEqual ("typeReference: resolution failure error msg", diag.getErrors().get(0), "Could not resolve type 'IntLiteral'");
        
        t = ref.resolve (r, new TestDiagnosticProducer());
        assertEqual ("typeReference: resolved reference", t, Optional.of(LiteralTypes.INTLITERAL));
        
        ref = new TypeReference(SID.from("value"));
        diag = new TestDiagnosticProducer();
        t = ref.resolve (r, diag);
        assertEqual ("typeReference: resolution to non-type", t, Optional.empty());
        assertEqual ("typeReference: resolution to non-type count", diag.getErrors().size(), 1);
        assertEqual ("typeReference: resolution to non-type msg", diag.getErrors().get(0), "Object 'value' is not a type");
    }
    
    public void functionType ()
    {
        Scope s = new Scope();
        s.addSymbol (new ConstSymbol("IntLiteral", LiteralTypes.INTLITERAL, CoreTypes.ANNOTATEDJAVATYPE));
        Resolver r = Resolver.newScope (Resolver.EMPTY, s);
        Type ref = new TypeReference(SID.from("IntLiteral"));
        Type fn = new FunctionType (
            Collections.singletonList(ref),
            Optional.of(ref)); // (IntLiteral) -> IntLiteral
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        Optional<Type> t = fn.resolve (r, diag);
        assertEqual ("functionType: param", ((FunctionType)t.get()).getElements().get(0).getParameters().get(0), LiteralTypes.INTLITERAL);
        assertEqual ("functionType: rtype", ((FunctionType)t.get()).getElements().get(0).getReturnType(), Optional.of(LiteralTypes.INTLITERAL));
    }
}
