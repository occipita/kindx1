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
import java.util.ArrayList;
import java.util.Optional;

public class InferenceTest2 extends Assertions implements Runnable
{
    public void run ()
    {
        invokeKnownFnInfersRet();
        invokeKnownFnInfersArgs();
        invokeOverloadFnInfersRet();
        invokeUnknownFnInfersType();
        invokeKnownFnDereferencesArgs();
        unaryOpInfersSub();
    }
    public void invokeKnownFnInfersRet ()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1"));
        TestType t2 = new TestType (SID.from("test::t2"));
        FunctionType ft = new FunctionType(Collections.singletonList(t1), Optional.of(t2));
        
        ConstVal fn = new ConstVal(null,ft);
        ConstVal arg = new ConstVal(null,t1);
        FunctionCall fc = new FunctionCall(fn, Collections.singletonList(arg));
        
        assertTrue ("invokeKnownFnInfersRet: inferTypesSilently succeeds", fc.inferTypesSilently (res, TypeSpec.UNSPECIFIED));
        assertTrue ("invokeKnownFnInfersRet: inferTypes succeeds", fc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("invokeKnownFnInfersRet: checkTypes succeeds", fc.checkTypes (diag));
        assertEqual ("invokeKnownFnInfersRet: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEqual ("invokeKnownFnInfersRet: final type", fc.getResultType(), Optional.of(t2));
    }
    public void invokeKnownFnInfersArgs ()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1"));
        TestType t2 = new TestType (SID.from("test::t2"));
        FunctionType ft = new FunctionType(Collections.singletonList(t1), Optional.of(t2));
        Type ti = tpc.addImplicit();
           
        ConstVal fn = new ConstVal(null,ft);
        ConstVal arg = new ConstVal(null,ti);
        FunctionCall fc = new FunctionCall(fn, Collections.singletonList(arg));
        
        fc.inferTypesSilently (res, TypeSpec.UNSPECIFIED);
        assertTrue ("invokeKnownFnInfersArgs: inferTypes succeeds", fc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("invokeKnownFnInfersArgs: checkTypes succeeds", fc.checkTypes (diag));
        assertEqual ("invokeKnownFnInfersArgs: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEqual ("invokeKnownFnInfersArgs: final type", fc.getResultType(), Optional.of(t2));
        assertEqual ("invokeKnownFnInfersArgs: type of arg", arg.getResultType(), Optional.of(t1));
    }
    public void invokeOverloadFnInfersRet ()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1"));
        TestType t2 = new TestType (SID.from("test::t2"));
        TestType t3 = new TestType (SID.from("test::t3"));
        FunctionType ft1 = new FunctionType(Collections.singletonList(t3), Optional.of(t1));
        FunctionType ft2 = new FunctionType(Collections.singletonList(t1), Optional.of(t2));
        FunctionType ft = new FunctionType(Arrays.asList(ft1,ft2));
        
        ConstVal fn = new ConstVal(null,ft);
        ConstVal arg = new ConstVal(null,t1);
        FunctionCall fc = new FunctionCall(fn, Collections.singletonList(arg));
        
        assertTrue ("invokeOverloadFnInfersRet: inferTypesSilently succeeds", fc.inferTypesSilently (res, TypeSpec.UNSPECIFIED));
        assertTrue ("invokeOverloadFnInfersRet: inferTypes succeeds", fc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("invokeOverloadFnInfersRet: checkTypes succeeds", fc.checkTypes (diag));
        assertEqual ("invokeOverloadFnInfersRet: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEqual ("invokeOverloadFnInfersRet: final type", fc.getResultType(), Optional.of(t2));
        
    }
    public void invokeUnknownFnInfersType ()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1"));
        TestType t2 = new TestType (SID.from("test::t2"));
        FunctionType ft = new FunctionType(Collections.singletonList(t1), Optional.of(t2));
        
        ConstVal fn = new ConstVal(null,tpc.addImplicit());
        ConstVal arg = new ConstVal(null,t1);
        FunctionCall fc = new FunctionCall(fn, Collections.singletonList(arg));
        
        fc.inferTypesSilently (res, TypeSpec.subtypeOf(t2));
        assertTrue ("invokeUnknownFnInfersType: inferTypes succeeds", fc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.subtypeOf(t2)));
        assertTrue ("invokeUnknownFnInfersType: checkTypes succeeds", fc.checkTypes (diag));
        assertEqual ("invokeUnknownFnInfersType: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEqual ("invokeUnknownFnInfersType: type of fn", fn.getResultType(), Optional.of(ft));
    }
    public void invokeKnownFnDereferencesArgs ()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1"));
        TestType t2 = new TestType (SID.from("test::t2"));
        FunctionType ft = new FunctionType(Collections.singletonList(t1), Optional.of(t2));
        
        ConstVal fn = new ConstVal(null,ft);
        ConstVal arg = new ConstVal(null,new Ref(t1));
        FunctionCall fc = new FunctionCall(fn, new ArrayList<>(Collections.singletonList(arg))); //list arg must be mutable
        
        fc.inferTypesSilently (res, TypeSpec.UNSPECIFIED);
        assertTrue ("invokeKnownFnDereferencesArgs: inferTypes succeeds", fc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("invokeKnownFnDereferencesArgs: checkTypes succeeds", fc.checkTypes (diag));
        assertEqual ("invokeKnownFnDereferencesArgs: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEqual ("invokeKnownFnDereferencesArgs: final type", fc.getResultType(), Optional.of(t2));
        assertEqual ("invokeKnownFnDereferencesArgs: type of arg0 evaluatable", fc.getArgs().get(0).getClass(), Dereference.class);
    }
    public void unaryOpInfersSub ()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY; // FIXME should be able to resolve an implementation of unary minus
        TestType t1 = new TestType (SID.from("test::t1"));
        TypeParameterContext.Parameter t2 = tpc.addImplicit();
        ConstVal v1 = new ConstVal(null,t2);
        UnaryOpApplication fc = new UnaryOpApplication(v1, "-");
        
        fc.inferTypesSilently (res, TypeSpec.subtypeOf(t1));
        assertTrue ("unaryOpInfersSub: inferTypes succeeds", fc.inferTypes (res, tpc, diag, TypeSpec.subtypeOf(t1)));
        assertTrue ("unaryOpInfersSub: checkTypes succeeds", fc.checkTypes (diag));
        assertEqual ("unaryOpInfersSub: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEqual ("unaryOpInfersSub: final type", fc.getResultType(), Optional.of(t1));
        assertEqual ("unaryOpInfersSub: arg type", fc.getSubExpr().getResultType(), Optional.of(t1));
    }
}
