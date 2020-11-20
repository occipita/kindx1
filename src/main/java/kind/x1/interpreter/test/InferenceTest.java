package kind.x1.interpreter.test;

import kind.x1.*;
import kind.x1.misc.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.symbols.ConstSymbol;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.executables.*;
import kind.x1.interpreter.types.primitive.*;

import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.util.Optional;

public class InferenceTest extends Assertions implements Runnable
{
    public void run ()
    {
        constValKnowsType();
        dotNoInferenceOnDeclared();
        dotOnImplicitParamAddsConstraint();
        dotResolvesProp();
        dotImplicitNoResultSpec();
        varRefResolves();
        varRefUnknown();
    }
    
    public void constValKnowsType()
    {
        ConstVal cv = new ConstVal(null, LiteralTypes.INTLITERAL);
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        assertTrue ("constValKnowsType: inferTypesSilently succeeds", cv.inferTypesSilently (Resolver.EMPTY, TypeSpec.UNSPECIFIED));
        assertTrue ("constVa>>>lKnowsType: inferTypes succeeds", cv.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("constValKnowsType: checkTypes succeeds", cv.checkTypes (diag));
        assertEqual ("constValKnowsType: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEqual ("constValKnowsType: final type", cv.getResultType(), Optional.of(LiteralTypes.INTLITERAL));
    }
    public void dotNoInferenceOnDeclared ()
    {
        TypeParameterContext tpc = new TypeParameterContext();
        Type t1 = tpc.addExplicit ("T1");
        DotApplication dotapp = new DotApplication(new ConstVal(null,t1), "unknown");
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        
        assertFalse ("dotNoInferenceOnDeclared: inference should be unsuccessful", 
            dotapp.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        
        assertEqual ("dotNoInferenceOnDeclared: no constraints added", tpc.getConstraints().size(), 0);
        assertEqual ("dotNoInferenceOnDeclared: error produced", 
            diag.getErrors().toString(), 
            "[Type 'T1' does not have a definition for 'unknown']");
    }
    public void dotOnImplicitParamAddsConstraint ()
    {
        TypeParameterContext tpc = new TypeParameterContext();
        Type t1 = tpc.addImplicit();
        DotApplication dotapp = new DotApplication(new ConstVal(null,t1), "property");
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        
        assertTrue ("dotOnImplicitParamAddsConstraint: inference should be successful", 
            dotapp.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.subtypeOf(LiteralTypes.INTLITERAL)));
        
        assertEqual ("dotOnImplicitParamAddsConstraint: constraints added", tpc.getConstraints().size(), 1);
        assertEqual (
            "dotOnImplicitParamAddsConstraint: constraint description", 
            tpc.getConstraints().get(0).getDescription(), 
            "kind::core::propertyReadable::property(_1, kind::core::IntLiteral)");
        assertEqual ("dotOnImplicitParamAddsConstraint: no error produced", diag.getErrors().size(), 0);
    }
    
    public void dotResolvesProp ()
    {
        TypeParameterContext tpc = new TypeParameterContext();
        TestType t1 = new TestType (SID.from("test::t1"));
        Type t2 = new TestType (SID.from("test::t2"));
        t1.addMember ("testProperty", t2);
        DotApplication dot = new DotApplication(new ConstVal(null, t1), "testProperty");
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        assertTrue ("dotResolvesProp: inferTypesSilently succeeds", dot.inferTypesSilently (Resolver.EMPTY, TypeSpec.UNSPECIFIED));
        assertTrue ("dotResolvesProp: inferTypes succeeds", dot.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("dotResolvesProp: checkTypes succeeds", dot.checkTypes (diag));
        assertEqual ("dotResolvesProp: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEqual ("dotResolvesProp: final type", dot.getResultType(), Optional.of(t2));
    }
    public void dotImplicitNoResultSpec ()
    {
        TypeParameterContext tpc = new TypeParameterContext();
        Type t1 = tpc.addImplicit();
        DotApplication dotapp = new DotApplication(new ConstVal(null,t1), "property");
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        
        assertTrue ("dotImplicitNoResultSpec: inference should be successful", 
            dotapp.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        
        assertEqual ("dotImplicitNoResultSpec: constraints added", tpc.getConstraints().size(), 1);
        assertEqual (
            "dotImplicitNoResultSpec: constraint description", 
            tpc.getConstraints().get(0).getDescription(), 
            "kind::core::propertyReadable::property(_1, _2)");
        assertEqual ("dotImplicitNoResultSpec: no error produced", diag.getErrors().size(), 0);
        assertEqual ("dotImplicitNoResultSpec: result type", dotapp.getResultType().get().getName(), "_2");        
    }
    public void varRefResolves()
    {
        VariableRef v = new VariableRef(SID.from("test"));
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Scope scope = new Scope();
        ConstSymbol sym = new ConstSymbol("test", null, LiteralTypes.INTLITERAL);
        scope.addSymbol (sym);
        Resolver res = Resolver.newScope(Resolver.EMPTY, scope);
        assertTrue ("varRefResolves: inferTypesSilently succeeds", v.inferTypesSilently (res, TypeSpec.UNSPECIFIED));
        assertTrue ("varRefResolves: inferTypes succeeds", v.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("varRefResolves: checkTypes succeeds", v.checkTypes (diag));
        assertEqual ("varRefResolves: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEqual ("varRefResolves: final type", v.getResultType(), Optional.of(LiteralTypes.INTLITERAL));
    }
    public void varRefUnknown()
    {
        VariableRef v = new VariableRef(SID.from("test"));
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        assertFalse ("varRefUnknown: inferTypesSilently fails", v.inferTypesSilently (res, TypeSpec.UNSPECIFIED));
        assertFalse ("varRefUnknown: inferTypes fails", v.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertEqual ("varRefUnknown: produces error", diag.getErrors().toString(), "[Could not resolve variable 'test']");
    }
}
