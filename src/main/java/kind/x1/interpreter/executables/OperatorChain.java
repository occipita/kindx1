package kind.x1.interpreter.executables;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.types.primitive.*;
import kind.x1.interpreter.values.KVal;
import kind.x1.interpreter.*;
import kind.x1.*;

/** A container for e  pator onstces*/
public class OperatorChain implements Evaluatable 
{
    public static class Operator
    {
        private String name;
        private boolean debug;
        
        public Operator(String n) 
        { 
            name = n; 
        }
        public String getName() { return name; }
        public Operator debug() { debug = true; return this; }
    }
    private List<Evaluatable> operands = new ArrayList<>();
    private List<Operator> operators = new ArrayList<>();
    private boolean rightAssoc;
    private Optional<Evaluatable> chain = Optional.empty();
    
    public OperatorChain (boolean rightAssoc)
    {
        this.rightAssoc = rightAssoc;
    }
    
    public List<Evaluatable> getOperands() { return operands; }
    public List<Operator> getOperators() { return operators; }
    public boolean isRightAssoc () { return rightAssoc; }     
    
    public Evaluatable getChain()
    {
        if (chain.isPresent()) return chain.get();
        // FIXME right assoc
        Evaluatable lhs = operands.get(0);
        for (int i = 0; i < operators.size(); i++)
        {
            Operator op = operators.get(i);
            lhs = new kind.x1.interpreter.executables.Operator (op.getName(), lhs, operands.get(i+1));
        }
        chain = Optional.of(lhs);
        return lhs;
    } 
    
    public boolean inferTypesSilently (Resolver resolver, TypeSpec target) 
    {
        return getChain().inferTypesSilently (resolver, target);
    }
    public boolean inferTypes (Resolver resolver, TypeParameterContext context, DiagnosticProducer diag, TypeSpec target) 
    {
        return getChain().inferTypes (resolver, context, diag, target);
    }
    public boolean checkTypes (DiagnosticProducer diag) 
    {
        return getChain().checkTypes (diag); 
    }

    public Optional<Type> getResultType () 
    { 
        return getChain().getResultType(); 
    }

    public Continuation execute (Resolver resolver, ExecutionContext context, BindableContinuation continuation)
    {
	return getChain().execute (resolver, context, continuation);
    }
}
