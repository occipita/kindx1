package kind.x1.interpreter.executables;

import kind.x1.ast.ExprVisitor;
import kind.x1.Factory;
import kind.x1.interpreter.values.literals.*;
import kind.x1.interpreter.types.primitive.*;
import kind.x1.misc.SID;

import java.math.BigInteger;
import java.util.ArrayList;


public class EvaluatableBuilder extends ExprVisitor
{
    private Evaluatable building;
    private EvaluatableBuilder subexprBuilder;
       
    public Evaluatable build () { return building; }  
    
    public LiteralFlagVisitor intLiteral (String value)
    {
        building = new ConstVal(new IntLiteral(value), LiteralTypes.INTLITERAL);
        return IGNORE_LITERAL_FLAGS; // FIXME
    }
    public void variableRef (SID id) {
        building = new VariableRef (id);
    }
    
    public void beginOperatorChainRight () { building = new OperatorChain (true); }
    public void beginOperatorChainLeft () { building = new OperatorChain (false); }
    public ExprVisitor operand () { 
        subexprBuilder = new EvaluatableBuilder();
        return subexprBuilder; 
    }
    public void operator (String operator) { 
        ((OperatorChain)building).getOperands().add(subexprBuilder.build());
        ((OperatorChain)building).getOperators().add(new OperatorChain.Operator(operator));
    }
    public void endOperatorChain () { 
        ((OperatorChain)building).getOperands().add(subexprBuilder.build());
    }

    public void applyDot (String id) {
        building = new DotApplication (building, id);
    }
    public void beginApplyFnCall () { 
        building = new FunctionCall (building, new ArrayList<>());
        subexprBuilder = null;
    }
    public ExprVisitor fnCallArgument () { 
        if (subexprBuilder != null) ((FunctionCall)building).getArgs().add(subexprBuilder.build());
        return operand();
    }
    public void endApplyFnCall () { 
        if (subexprBuilder != null) ((FunctionCall)building).getArgs().add(subexprBuilder.build());
    }
    public void applyUnaryOp(String id)
    {
        building = new UnaryOpApplication(building, id);
    }
    public static final Factory<EvaluatableBuilder> FACTORY = new Factory<EvaluatableBuilder> () {
        public EvaluatableBuilder create () { return new EvaluatableBuilder(); }
    }; 
}
