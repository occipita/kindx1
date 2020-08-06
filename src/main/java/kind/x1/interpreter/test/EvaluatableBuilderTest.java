package kind.x1.interpreter.test;

import kind.x1.*;
import kind.x1.ast.*;
import kind.x1.misc.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.executables.*;
import kind.x1.interpreter.values.literals.*;
import kind.x1.interpreter.types.primitive.*;

import java.util.List;
import java.util.Collections;

public class EvaluatableBuilderTest extends Assertions implements Runnable
{
    public void run ()
    {
        intLiteral(); 
        singleBinaryOperator ();
        dotApplication();     
        variableRef();
        fnApplication();
        unaryOpApplication ();
    }
    
    public void intLiteral ()
    {
        EvaluatableBuilder b = new EvaluatableBuilder();
        b.intLiteral("42");
        Evaluatable e = b.build();
        assertEqual ("intLiteral: type", e.getClass(), ConstVal.class);
        ConstVal cv = (ConstVal)e;
        assertEqual ("intLiteral: kind type", cv.getType(), LiteralTypes.INTLITERAL);
        assertEqual ("intLiteral: value", ((IntLiteral)cv.getValue()).getValue().longValue(), 42L);
    }
    // FIXME int literal with flags
    
    public void singleBinaryOperator ()
    {
        EvaluatableBuilder b = new EvaluatableBuilder();
        b.beginOperatorChainLeft();
        b.operand().intLiteral("1");
        b.operator("+");
        b.operand().intLiteral("2");
        b.endOperatorChain();
        Evaluatable e = b.build();
        assertEqual ("singleBinaryOperator: type", e.getClass(), OperatorChain.class);
        OperatorChain oc = (OperatorChain)e;
        assertFalse ("singleBinaryOperator: should be left associative", oc.isRightAssoc());
        ConstVal op0 = (ConstVal)oc.getOperands().get(0);
        assertEqual ("singleBinaryOperator: operand 0", ((IntLiteral)op0.getValue()).getValue().longValue(), 1L);
        ConstVal op1 = (ConstVal)oc.getOperands().get(1);
        assertEqual ("singleBinaryOperator: operand 1", ((IntLiteral)op1.getValue()).getValue().longValue(), 2L);
        OperatorChain.Operator op = oc.getOperators().get(0);
        assertEqual ("singleBinaryOperator: operator", op.getName(), "+");
    }
    
    public void dotApplication ()
    {
        EvaluatableBuilder b = new EvaluatableBuilder();
        b.intLiteral("1");
        b.applyDot("asInt32");
        Evaluatable e = b.build();
        assertEqual ("dotApplication: type", e.getClass(), DotApplication.class);
        DotApplication oc = (DotApplication)e;
        assertEqual ("dotApplication: subexpr type", oc.getSubExpr().getClass(), ConstVal.class);
        assertEqual ("dotApplication: id", oc.getId(), "asInt32");
    }
    
    public void variableRef ()
    {
        EvaluatableBuilder b = new EvaluatableBuilder();
        b.variableRef(SID.from("id"));
        Evaluatable e = b.build();
        assertEqual ("variableRef: type", e.getClass(), VariableRef.class);
        VariableRef v = (VariableRef)e;
        assertEqual ("variableRef: id", v.getId(), SID.from("id"));
    }

    public void fnApplication ()
    {
        EvaluatableBuilder b = new EvaluatableBuilder();
        b.intLiteral("1"); // obviously this isn't well typed, but we don't check...
        b.beginApplyFnCall ();
        b.fnCallArgument ().intLiteral("2");
        b.fnCallArgument ().intLiteral("3");
        b.endApplyFnCall ();
        Evaluatable e = b.build();
        assertEqual ("fnApplication: type", e.getClass(), FunctionCall.class);
        FunctionCall oc = (FunctionCall)e;
        assertEqual ("fnApplication: subexpr type", oc.getSubExpr().getClass(), ConstVal.class);
        assertEqual ("fnApplication: subexpr value", ((IntLiteral)((ConstVal)oc.getSubExpr()).getValue()).getValue().longValue(), 1L);
        assertEqual ("fnApplication: arg 0 type", oc.getArgs().get(0).getClass(), ConstVal.class);
        assertEqual ("fnApplication: arg 0 value", ((IntLiteral)((ConstVal)oc.getArgs().get(0)).getValue()).getValue().longValue(), 2L);
        assertEqual ("fnApplication: arg 1 type", oc.getArgs().get(1).getClass(), ConstVal.class);
        assertEqual ("fnApplication: arg 1 value", ((IntLiteral)((ConstVal)oc.getArgs().get(1)).getValue()).getValue().longValue(), 3L);
    }

    public void unaryOpApplication ()
    {
        EvaluatableBuilder b = new EvaluatableBuilder();
        b.intLiteral("1");
        b.applyUnaryOp("-");
        Evaluatable e = b.build();
        assertEqual ("unaryOpApplication: type", e.getClass(), UnaryOpApplication.class);
        UnaryOpApplication oc = (UnaryOpApplication)e;
        assertEqual ("unaryOpApplication: subexpr type", oc.getSubExpr().getClass(), ConstVal.class);
        assertEqual ("unaryOpApplication: id", oc.getId(), "-");
    }

}
