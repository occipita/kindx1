package kind.x1.interpreter.executables;

import kind.x1.interpreter.types.*;
import kind.x1.interpreter.values.KVal;
import kind.x1.interpreter.values.KCallable;
import kind.x1.interpreter.*;
import kind.x1.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class FunctionCall implements Evaluatable
{
    private Evaluatable subExpr;
    private List<Evaluatable> args;
    private Optional<Type> resultType = Optional.empty();
        
    public FunctionCall(Evaluatable sub, List<Evaluatable> args) { subExpr = sub; this.args = args; }
    public Evaluatable getSubExpr () { return subExpr; }
    public List<Evaluatable> getArgs () { return args; }
    public String toString () { return "<call " + subExpr + " " + args + ">"; }
    
    public boolean inferTypesSilently (Resolver resolver, TypeSpec target) 
    { 
        if (resultType.isPresent()) return true;
        for (Evaluatable e : args) e.inferTypesSilently(resolver, TypeSpec.UNSPECIFIED);
        TypeSpec ft = inferFunctionType(target);
        subExpr.inferTypesSilently(resolver, ft);
        // fixme - may want to rerun arg inference if function type now known & monomorphic
        //for (Evaluatable e : args) e.inferTypesSilently(resolver, targetArgType);
        inferResult();
        return resultType.isPresent(); 
    }
    public boolean inferTypes (Resolver resolver, TypeParameterContext context, DiagnosticProducer diag, TypeSpec expected) 
    { 
        if (resultType.isPresent()) return true;
        // FIXME may be able to infer function type (if args & expected known)
        TypeSpec ft = inferFunctionType(expected);
        subExpr.inferTypes(resolver, context, diag, ft);
        Optional<Type> ftype = subExpr.getResultType();
        if (ftype.isPresent() && adjustFnType (ftype.get(), ft)) {
            // function type has changed, so re-run inference:
            subExpr.inferTypes(resolver, context, diag, ft);
            ftype = subExpr.getResultType();
        }
        if (ftype.isPresent() && ftype.get() instanceof FunctionType && ((FunctionType)ftype.get()).getElements().size() == 1)
        {
            // function type is known amd monomorphic, so can infer arg types if required
            List<Type> params = ((FunctionType)ftype.get()).getElements().get(0).getParameters();
            //System.out.println ("inferring monofn: " + args + " -> " + params);
            if (params.size() != args.size()) return false;
            for (int i = 0; i < params.size(); i ++)
            {
                TypeSpec spec = TypeSpec.subtypeOf(params.get(i));
                args.get(i).inferTypes(resolver, context, diag, spec);
                Optional<Type> ot = args.get(i).getResultType();
                //System.out.println ("  "+ot+" -> "+params.get(i));
                if (ot.isPresent() && adjustArgType (i, ot.get(), spec))
                {
                    // re-run to allow the arg to resolve its updated type
                    args.get(i).inferTypes(resolver, context, diag, spec);
                }
            }
        }
        else
            for (Evaluatable e : args) e.inferTypes(resolver, context, diag, TypeSpec.UNSPECIFIED);
        inferResult();
        return resultType.isPresent(); 
    }
    private TypeSpec inferFunctionType(TypeSpec returnSpec)
    {
        // we can infer the function type if we have a return type specification
        // and all argument types are known
        if (returnSpec == TypeSpec.UNSPECIFIED) return TypeSpec.UNSPECIFIED;
        List<Type> argTypes = new ArrayList<>(args.size());
        for (int i = 0 ; i < args.size(); i ++)
        {
            Optional<Type> t = args.get(i).getResultType();
            if (!t.isPresent()) return TypeSpec.UNSPECIFIED;
            argTypes.add (t.get());
        }
        return TypeSpec.subtypeOf(new FunctionType(argTypes,returnSpec.getInferredType()));
    }
    private boolean adjustFnType (Type t, TypeSpec spec)
    {
        if (t instanceof Ref && !spec.isRefType())
        {
            subExpr = new Dereference (subExpr);
            t = Ref.strip(t);
        }


        if (spec.matches(t)) return false;
        if (t instanceof TypeParameterContext.Parameter && 
                    ((TypeParameterContext.Parameter)t).isImplicit() &&
                    ((TypeParameterContext.Parameter)t).isFullyResolved()) // fully resolved => not already unified
        {
            ((TypeParameterContext.Parameter)t).unifyWith(spec.getInferredType());
            return true;
        }
        return false;
    }
    private boolean adjustArgType (int index, Type t, TypeSpec spec)
    {
        if (t instanceof Ref && !spec.isRefType())
        {
            args.set(index, new Dereference (args.get(index)));
            t = Ref.strip(t);
        }

        if (spec.matches(t)) return false;
        if (t instanceof TypeParameterContext.Parameter && 
                    ((TypeParameterContext.Parameter)t).isImplicit() &&
                    ((TypeParameterContext.Parameter)t).isFullyResolved()) // fully resolved => not already unified
        {
            ((TypeParameterContext.Parameter)t).unifyWith(spec.getInferredType());
            return true;
        }
        return false;
    }
    private void inferResult ()
    {
        try
        {
            Type fn = subExpr.getResultType().get();
            if (!(fn instanceof FunctionType)) return;
            List<Type> argTypes = new ArrayList<>();
            for (Evaluatable arg : args) argTypes.add(arg.getResultType().get());
            resultType = ((FunctionType)fn).getApplicationResult(argTypes); 
        }
        catch (NoSuchElementException ignored) {}
    }
    
    public boolean checkTypes (DiagnosticProducer diag) { /*FIXME*/ return true; }
     
    public Optional<Type> getResultType () { return resultType; }


    public Continuation execute (Resolver resolver, ExecutionContext context, BindableContinuation continuation)
    {
	// evaluate function
	return subExpr.execute (resolver, context,
		fn -> Continuations.mapList (args, resolver, context,         // evaluate arguments
		     args -> ((KCallable)fn).call (   // execute function with null thisArg
			 args, null, resolver, context, continuation)));
    }
    
}
