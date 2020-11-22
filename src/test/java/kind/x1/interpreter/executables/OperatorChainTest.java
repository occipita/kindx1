package kind.x1.interpreter.executables;

import java.util.*;
import java.util.concurrent.atomic.*;
import org.junit.*;
import static org.junit.Assert.*;

import kind.x1.misc.SID;
import kind.x1.interpreter.*;
import kind.x1.interpreter.values.*;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.test.*;
import kind.x1.interpreter.symbols.ConstSymbol;
import kind.x1.interpreter.types.primitive.*;

public class OperatorChainTest
{
    @Test
    public void opChainInfersTargetsLTR()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1"));
	t1.addOperator("+", t1, t1, (a,b)->a);
        ConstVal e1 = new ConstVal (null, t1);
        ConstVal e2 = new ConstVal (null, tpc.addImplicit());
        OperatorChain oc = new OperatorChain(false);
        oc.getOperands().add(e1);
        oc.getOperands().add(e2);
        oc.getOperators().add(new OperatorChain.Operator("+")); // triggers pattern forall T . (TxT)->T
        
        assertTrue ("opChainInfersTargetsLTR: inferTypesSilently succeeds", oc.inferTypesSilently (res, TypeSpec.UNSPECIFIED));
        assertTrue ("opChainInfersTargetsLTR: inferTypes succeeds", oc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("opChainInfersTargetsLTR: checkTypes succeeds", oc.checkTypes (diag));
        assertEquals ("opChainInfersTargetsLTR: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEquals ("opChainInfersTargetsLTR: final type", oc.getResultType(), Optional.of(t1));
        assertEquals ("opChainInfersTargetsLTR: type of e2", e2.getResultType(), Optional.of(t1));
    }
    @Test
    public void opChainInfersTargetsRTL()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1"));
	t1.addOperator("+", t1, t1, (a,b)->a);
        ConstVal e2 = new ConstVal (null, t1);
        ConstVal e1 = new ConstVal (null, tpc.addImplicit());
        OperatorChain oc = new OperatorChain(false);
        oc.getOperands().add(e1);
        oc.getOperands().add(e2);
        oc.getOperators().add(new OperatorChain.Operator("+")); // triggers pattern forall T . (TxT)->T
        
        assertTrue ("opChainInfersTargetsRTL: inferTypesSilently succeeds", oc.inferTypesSilently (res, TypeSpec.UNSPECIFIED));
        assertTrue ("opChainInfersTargetsRTL: inferTypes succeeds", oc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("opChainInfersTargetsRTL: checkTypes succeeds", oc.checkTypes (diag));
        assertEquals ("opChainInfersTargetsRTL: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEquals ("opChainInfersTargetsRTL: final type", oc.getResultType(), Optional.of(t1));
        assertEquals ("opChainInfersTargetsRTL: type of e1", e1.getResultType(), Optional.of(t1));
    }
    @Test
    public void opChainInfersTargetsLong()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1"));
	t1.addOperator("+", t1, t1, (a,b)->a);
	t1.addOperator("-", t1, t1, (a,b)->a);
	t1.addOperator("*", t1, t1, (a,b)->a);
	t1.addOperator("/", t1, t1, (a,b)->a);
        ConstVal e1 = new ConstVal (null, tpc.addImplicit());
        ConstVal e2 = new ConstVal (null, tpc.addImplicit());
        ConstVal e3 = new ConstVal (null, t1);
        ConstVal e4 = new ConstVal (null, tpc.addImplicit());
        ConstVal e5 = new ConstVal (null, tpc.addImplicit());
        OperatorChain oc = new OperatorChain(false);
        oc.getOperands().add(e1);
        oc.getOperands().add(e2);
        oc.getOperands().add(e3);
        oc.getOperands().add(e4);
        oc.getOperands().add(e5);
        oc.getOperators().add(new OperatorChain.Operator("+")); // triggers pattern forall T . (TxT)->T
        oc.getOperators().add(new OperatorChain.Operator("-")); // triggers pattern forall T . (TxT)->T
        oc.getOperators().add(new OperatorChain.Operator("*")); // triggers pattern forall T . (TxT)->T
        oc.getOperators().add(new OperatorChain.Operator("/")); // triggers pattern forall T . (TxT)->T
        
        assertTrue ("opChainInfersTargetsLong: inferTypesSilently succeeds", oc.inferTypesSilently (res, TypeSpec.UNSPECIFIED));
        assertTrue ("opChainInfersTargetsLong: inferTypes succeeds", oc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("opChainInfersTargetsLong: checkTypes succeeds", oc.checkTypes (diag));
        assertEquals ("opChainInfersTargetsLong: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEquals ("opChainInfersTargetsLong: final type", oc.getResultType(), Optional.of(t1));
        assertEquals ("opChainInfersTargetsLong: type of e1", e1.getResultType(), Optional.of(t1));
        assertEquals ("opChainInfersTargetsLong: type of e5", e5.getResultType(), Optional.of(t1));
    }
    @Test
    public void assignmentInfersRef ()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1"));
        ConstVal e1 = new ConstVal (null, tpc.addImplicit());
        ConstVal e2 = new ConstVal (null, t1);
        OperatorChain oc = new OperatorChain(false);
        oc.getOperands().add(e1);
        oc.getOperands().add(e2);
        oc.getOperators().add(new OperatorChain.Operator("=")); // forall T . (ref(T)xT)->T
        
        assertTrue ("assignmentInfersRef: inferTypesSilently succeeds", oc.inferTypesSilently (res, TypeSpec.UNSPECIFIED));
        assertTrue ("assignmentInfersRef: inferTypes succeeds", oc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("assignmentInfersRef: checkTypes succeeds", oc.checkTypes (diag));
        assertEquals ("assignmentInfersRef: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEquals ("assignmentInfersRef: final type", oc.getResultType(), Optional.of(t1));
        assertEquals ("assignmentInfersRef: type of e1", e1.getResultType(), Optional.of(new Ref(t1)));
    }
    @Test
    public void comparisonInfersBoolean ()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1"));
	t1.addOperator("==", t1, CoreTypes.BOOLEAN, (a,b)->null);
        ConstVal e1 = new ConstVal (null, tpc.addImplicit());
        ConstVal e2 = new ConstVal (null, t1);
        OperatorChain oc = new OperatorChain(false);
        oc.getOperands().add(e1);
        oc.getOperands().add(e2);
        oc.getOperators().add(new OperatorChain.Operator("==")); // forall T . (TxT)->boolean
        
        assertTrue ("comparisonInfersBoolean: inferTypesSilently succeeds", oc.inferTypesSilently (res, TypeSpec.UNSPECIFIED));
        assertTrue ("comparisonInfersBoolean: inferTypes succeeds", oc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("comparisonInfersBoolean: checkTypes succeeds", oc.checkTypes (diag));
        assertEquals ("comparisonInfersBoolean: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEquals ("comparisonInfersBoolean: final type", oc.getResultType(), Optional.of(CoreTypes.BOOLEAN));
        assertEquals ("comparisonInfersBoolean: type of e1", e1.getResultType(), Optional.of(t1));
    }
    @Test
    public void opChainDereferences ()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1"));
	t1.addOperator("+", t1, t1, (a,b)->a);
        ConstVal e1 = new ConstVal (null, tpc.addImplicit());
        ConstVal e2 = new ConstVal (null, new Ref (t1));
        OperatorChain oc = new OperatorChain(false);
        oc.getOperands().add(e1);
        oc.getOperands().add(e2);
        oc.getOperators().add(new OperatorChain.Operator("+")); // forall T . (TxT)->T
        
        assertTrue ("opChainDereferences: inferTypesSilently succeeds", oc.inferTypesSilently (res, TypeSpec.UNSPECIFIED));
        assertTrue ("opChainDereferences: inferTypes succeeds", oc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertTrue ("opChainDereferences: checkTypes succeeds", oc.checkTypes (diag));
        assertEquals ("opChainDereferences: should not produce any errors", diag.getErrors().toString(), "[]"); 
        assertEquals ("opChainDereferences: final type", oc.getResultType(), Optional.of(t1));
        assertEquals ("opChainDereferences: type of e1", e1.getResultType(), Optional.of(t1));
    }
    @Test
    public void checksOperatorDefined ()
    {
        TestDiagnosticProducer diag = new TestDiagnosticProducer();
        TypeParameterContext tpc = new TypeParameterContext();
        Resolver res = Resolver.EMPTY;
        TestType t1 = new TestType (SID.from("test::t1")); // t1 does not define operator '+'
        ConstVal e1 = new ConstVal (null, tpc.addImplicit());
        ConstVal e2 = new ConstVal (null, new Ref (t1));
        OperatorChain oc = new OperatorChain(false);
        oc.getOperands().add(e1);
        oc.getOperands().add(e2);
        oc.getOperators().add(new OperatorChain.Operator("+")); // forall T . (TxT)->T
        
        assertTrue ("inferTypesSilently succeeds", oc.inferTypesSilently (res, TypeSpec.UNSPECIFIED));
        assertTrue ("inferTypes succeeds", oc.inferTypes (Resolver.EMPTY, tpc, diag, TypeSpec.UNSPECIFIED));
        assertFalse ("checkTypes fails", oc.checkTypes (diag));
        assertEquals ("should have produced error message", diag.getErrors().toString(), "[Operator '+' undefined for arguments (test::t1, test::t1)]"); 
    }
}
