package kind.x1.interpreter.executables;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Collections;
import java.util.function.Function;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.types.primitive.*;
import kind.x1.interpreter.values.*;
import kind.x1.interpreter.*;
import kind.x1.*;


public class Operator implements Evaluatable 
{
    private enum TypeRule
    {
        NONE { 
            boolean apply (Operator op, TypeSpec target) { return false; } 
        },
        ARITHMETIC {
            boolean apply (Operator op, TypeSpec target)
            {
                // FIXME wwe have nowhere near enough tests for this
                // FIXME is more  specific the best test?
                Optional<Type> t = op.getLHSType().map(Ref.STRIPPER); 
                t = selectMostSpecificType(t, op.getRHSType() .map(Ref.STRIPPER) ); 
                t = selectMostSpecificType(t, target.getInferredType() .map(Ref.STRIPPER) ); 
                t = selectMostSpecificType(t, op.lhsTarget.getInferredType() .map(Ref.STRIPPER) ); 
                t = selectMostSpecificType(t, op.rhsTarget.getInferredType() .map(Ref.STRIPPER) ); 
                if (op.debug) System.out.println ("ARITHMETIC.apply(): target: "+target+" chosen: "+t);
                if (t.isPresent())
                {
                    TypeSpec updatedTarget = TypeSpec.subtypeOf(t.get());
                    boolean updated = false;
                    if (updatedTarget.isMoreSpecificThan(op.lhsTarget)) {
                        updated = true;
                        op.lhsTarget = updatedTarget;
                    }
                    if (updatedTarget.isMoreSpecificThan(op.rhsTarget)) {
                        updated = true;
                        op.rhsTarget = updatedTarget;
                    }       
                    op.resultType = t;
                    return updated;
                }
                return false;
            }
        },
        COMPARISON {
            boolean apply (Operator op, TypeSpec target)
            {
                            // FIXME wwe have nowhere near enough tests for this
                // FIXME is more  specific the best test?
                Optional<Type> t = op.getLHSType() .map(Ref.STRIPPER) ; 
                t = selectMostSpecificType(t, op.getRHSType() .map(Ref.STRIPPER) ); 
                t = selectMostSpecificType(t, op.lhsTarget.getInferredType() .map(Ref.STRIPPER) ); 
                t = selectMostSpecificType(t, op.rhsTarget.getInferredType() .map(Ref.STRIPPER) ); 
                if (op.debug) System.out.println ("COMPARISON.apply(index): target: "+target+" chosen: "+t);
                boolean updated = false;
                if (!op.resultType.isPresent())
                {
                    op.resultType = Optional.of(CoreTypes.BOOLEAN);
                    updated = true;
                }
                if (t.isPresent())
                {
                    TypeSpec updatedTarget = TypeSpec.subtypeOf(t.get());
                    if (updatedTarget.isMoreSpecificThan(op.lhsTarget)) {
                        updated = true;
                        op.lhsTarget = updatedTarget;
                    }
                    if (updatedTarget.isMoreSpecificThan(op.rhsTarget)) {
                        updated = true;
                        op.rhsTarget = updatedTarget;
                    }       
                }
                return updated;
            }
        },
        ASSIGNMENT {
            boolean apply (Operator op, TypeSpec target)
            {
                // FIXME wwe have nowhere near enough tests for this
                // FIXME is more  specific the best test?
                Optional<Type> t = op.getLHSType() .map(Ref.STRIPPER) ;
                t = selectMostSpecificType(t, op.getRHSType() .map(Ref.STRIPPER) ); 
                t = selectMostSpecificType(t, target.getInferredType() .map(Ref.STRIPPER) ); 
                t = selectMostSpecificType(t, op.lhsTarget.getInferredType() .map(Ref.STRIPPER) ); 
                t = selectMostSpecificType(t, op.rhsTarget.getInferredType() .map(Ref.STRIPPER) ); 
                if (op.debug) System.out.println ("ASSIGNMENT.apply(index): target: "+target+" chosen: "+t);
                if (t.isPresent())
                {
                    TypeSpec updatedTarget = TypeSpec.subtypeOf(t.get());
                    TypeSpec refTarget = TypeSpec.exactly(new Ref(t.get()));
                    boolean updated = false;
                    if (refTarget.isMoreSpecificThan(op.lhsTarget)) {
                        updated = true;
                        op.lhsTarget = refTarget;
                    }
                    if (updatedTarget.isMoreSpecificThan(op.rhsTarget)) {
                        updated = true;
                        op.rhsTarget = updatedTarget;
                    }       
                    op.resultType = t;
                    return updated;
                }
                return false;
            }
        };
        
        abstract boolean apply (Operator op, TypeSpec target); 
    }
    private static HashMap<String, TypeRule> operatorTypeRules = new HashMap<>();
    static
    {
        operatorTypeRules.put ("+", TypeRule.ARITHMETIC);
        operatorTypeRules.put ("-", TypeRule.ARITHMETIC);
        operatorTypeRules.put ("*", TypeRule.ARITHMETIC);
        operatorTypeRules.put ("/", TypeRule.ARITHMETIC);
        
        operatorTypeRules.put ("==", TypeRule.COMPARISON);
        
        operatorTypeRules.put ("=", TypeRule.ASSIGNMENT);
        // FIXME more rules
    }

    private String name;
    private Evaluatable lhs, rhs;
    
    private TypeRule typeRule;
    private Optional<Type> resultType = Optional.empty();
    private TypeSpec lhsTarget = TypeSpec.UNSPECIFIED;
    private TypeSpec rhsTarget = TypeSpec.UNSPECIFIED;
    private boolean debug;
    private Optional<Type> operatorType = Optional.empty();
    private Function<KVal,KCallable> operatorImpl = null;
    
    public Operator(String n, Evaluatable lhs, Evaluatable rhs) 
    { 
        name = n; 
        typeRule = operatorTypeRules.get(n);
        if (typeRule == null) typeRule = TypeRule.NONE;
        this.lhs = lhs;
        this.rhs = rhs;
    }
    public String getName() { return name; }
    public Operator debug() { debug = true; return this; }

    private Optional<Type> getLHSType() { return lhs.getResultType(); }
    private Optional<Type> getRHSType() { return rhs.getResultType(); }

    public boolean inferTypesSilently (Resolver resolver, TypeSpec target) 
    {
        boolean updated;
        do
        {
            updated = typeRule.apply(this, target);
            // LTR pass
            lhs.inferTypesSilently (resolver, lhsTarget);
            rhs.inferTypesSilently (resolver, rhsTarget);
	    // FIXME attempt to infer based on defined types of operator
            updated = typeRule.apply(this, target) || updated;
            if (debug) dumpOp();
            if (!updated) break;
            // RTL pass
            updated = false;
            rhs.inferTypesSilently (resolver, rhsTarget);
            lhs.inferTypesSilently (resolver, lhsTarget);
	    // FIXME attempt to infer based on defined types of operator
            updated = typeRule.apply(this, target) || updated;
            if (debug) dumpOp();
        } 
        while(updated);
        return true; 
    }
    public boolean inferTypes (Resolver resolver, TypeParameterContext context, DiagnosticProducer diag, TypeSpec target) 
    {
        boolean typesUpdated;
        do
        {
            typesUpdated = typeRule.apply(this, target);
            lhs.inferTypes (resolver, context, diag, lhsTarget);
            typesUpdated = ensureMatch (lhsTarget, lhs.getResultType(), context, diag, true) || typesUpdated;
            rhs.inferTypes (resolver, context, diag, rhsTarget);
            typesUpdated = ensureMatch (rhsTarget, rhs.getResultType(), context, diag, false) || typesUpdated;
            if (debug) dumpOp();
        } while(typesUpdated);
        return true; 
    }
    private void dumpOp ()
    {
        Operator op=this;//fixme inline
        System.out.println ("op ("+op.getName() + ") lhs target: " + op.lhsTarget + " lhs: " + lhs.getResultType() + 
                 " rhs target: " + op.rhsTarget + " rhs: " + rhs.getResultType() + 
                 " result: " + op.resultType);
    }
    private boolean ensureMatch (TypeSpec spec, Optional<Type> type, TypeParameterContext context, DiagnosticProducer diag, boolean lhs)
    {
        if (!type.isPresent()) return false;
        Type t = type.get ();
        
        if (t instanceof Ref && !spec.isRefType())
        {
            setOperand (lhs, new Dereference (getOperand(lhs)));
            t = Ref.strip(t);
        }

        if (!spec.matches (t))
        {
            if (t instanceof TypeParameterContext.Parameter && 
                ((TypeParameterContext.Parameter)t).isImplicit() &&
                ((TypeParameterContext.Parameter)t).isFullyResolved()) // fully resolved => not already unified
            {
                ((TypeParameterContext.Parameter)t).unifyWith(spec.getInferredType());
                return true;
            }
            // FIXME scan scopes for objects that can injectc ode to resolve this discrepancy
        }
        return false;
    } 
    public boolean checkTypes (DiagnosticProducer diag) 
    {
        // FIXME check operands
	// FIXME this should be done during inference, when we have a Resolver... 
	Type tl = lhs.getResultType().orElse(Type.VOID);
	Type tr = rhs.getResultType().orElse(Type.VOID);
	if (!operatorType.isPresent() && tl instanceof MemberResolver)
	{
	    MemberResolver mr = ((MemberResolver)tl);
	    operatorType = mr.getMemberOperatorType(name);
	    operatorImpl = val -> mr.getMemberOperator (val, name).orElse (null);
	}
	//System.out.printf ("Operator.checkTypes: %s %s %s -> %s\n", tl.getName(), name, tr.getName(), operatorType.map(Type::getName));

	// check operator has been resolved and has correct type
	if (!operatorType.isPresent() || !(operatorType.get() instanceof FunctionType))
	{
	    diag.error (String.format (
			    "Operator '%s' undefined for arguments (%s, %s)",
			    name, tl.getName(), tr.getName()));
	    return false;
	}
        return true; 
    }

    public Optional<Type> getResultType () 
    { 
        return resultType; 
    }
    
    public Evaluatable getOperand (boolean left)
    {
        if (left) return lhs; else return rhs;
    }
    public void setOperand (boolean left, Evaluatable op)
    {
        if (left) lhs = op; else rhs = op;
    }
    
    private static Optional<Type> selectMostSpecificType(Optional<Type> t1, Optional<Type> t2)
    {
        if (!t1.isPresent()) return t2;
        if (!t2.isPresent()) return t1;
        Type tt1 = t1.get(), tt2 = t2.get();
        // prefer typesthat are concrete rather than parameters
        if (tt2 instanceof TypeParameterContext.Parameter) return t1;
        if (tt1 instanceof TypeParameterContext.Parameter) return t2;
        // fixme subtypes
        return t1;
    }

    public Continuation execute (Resolver resolver, ExecutionContext context, BindableContinuation continuation)
    {
	return lhs.execute (resolver, context, lhsVal ->
			    rhs.execute (resolver, context, rhsVal ->
					 operatorImpl.apply(lhsVal).call (
					     Collections.singletonList(rhsVal), null, Collections.emptyList(),
					     resolver, context, continuation)));
    }
}
