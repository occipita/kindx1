package kind.x1.interpreter.executables;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.types.primitive.*;
import kind.x1.interpreter.values.KVal;
import kind.x1.interpreter.*;
import kind.x1.*;
import kind.x1.misc.SID;

public class UnaryOpApplication implements Evaluatable
{
    private enum TypeRule
    {
        NONE { 
            TypeSpec subExprTarget (TypeSpec target) { return TypeSpec.UNSPECIFIED; } 
        },
        ARITHMETIC {
            TypeSpec subExprTarget (TypeSpec target)
            {
                return target;
            }
        };
        abstract TypeSpec subExprTarget (TypeSpec target);
    }
    private static HashMap<String,TypeRule> rules = new HashMap<>();
    static {
        rules.put("-", TypeRule.ARITHMETIC);
        rules.put("~", TypeRule.ARITHMETIC);
    }
    private Evaluatable subExpr;
    private String id;
    private TypeRule typeRule;
    private Optional<Type> resultType = Optional.empty();
        
    public UnaryOpApplication(Evaluatable sub, String id) { 
        subExpr = sub; 
        this.id = id;
        this.typeRule = rules.get (id);
        if (typeRule == null)  typeRule = TypeRule.NONE;
    }
    public Evaluatable getSubExpr () { return subExpr; }
    public String getId () { return id; }

    public boolean inferTypesSilently (Resolver resolver, TypeSpec target) 
    { 
        if (resultType.isPresent()) return true;
        // FIXME may be able to infer reqd type of subexpr
        if (!subExpr.inferTypesSilently(resolver, typeRule.subExprTarget(target))) return false;
        resolveOperator (subExpr.getResultType(), resolver);
        return resultType.isPresent(); 
    }
    public boolean inferTypes (Resolver resolver, TypeParameterContext context, DiagnosticProducer diag, TypeSpec target) 
    { 
        if (resultType.isPresent()) return true;
        // FIXME may be able to infer reqd type of subexpr
        TypeSpec subExprTarget = typeRule.subExprTarget(target);
        if (!subExpr.inferTypes(resolver, context, diag, subExprTarget)) return false;
        Type lhs = subExpr.getResultType().get();
        resolveOperator (subExpr.getResultType(), resolver);
        if (!resultType.isPresent ()) {
            // if type is an implicit parameter, we may be able to infer a constraint:
            if (lhs instanceof TypeParameterContext.Parameter && ((TypeParameterContext.Parameter)lhs).isImplicit())
            {/*
                Type rt;
                switch (subExprTarget.getMode())
                {
                    case UNSPECIFIED:
                        rt = context.addImplicit();
                        break;
                    default:
                        rt = subExprTarget.getType();
                }
                ((TypeParameterContext.Parameter)lhs).addInferredConstraint (
                    new Constraint(
                        SID.from("kind::core::hasUnaryOp").append( FIXME convert to a legal id id),
                            Arrays.asList(
                                lhs,
                                rt)));
                resultType = Optional.of(rt);
                return true;*/
                
                if (subExprTarget != TypeSpec.UNSPECIFIED) {
                    ((TypeParameterContext.Parameter)lhs).unifyWith (subExprTarget.getType());
                    subExpr.inferTypes(resolver, context, diag, subExprTarget);
                    resolveOperator (subExpr.getResultType(), resolver);
                    if (resultType.isPresent()) return true;
                }
            }
            diag.error ("Type '"+lhs.getName()+"' does not have a definition for unary operator '"+id+"'");
            return false;
        }
        return true; 
    }
    private void resolveOperator (Optional<Type> arg, Resolver resolver)
    {
        // Fixme need a real implementation
        if (!arg.isPresent()) return;
        if (arg.get() instanceof TypeParameterContext.Parameter) // FIXME check presence of appropriate constraint
            return;
        if (typeRule == TypeRule.ARITHMETIC) resultType = arg;
    }
    public boolean checkTypes (DiagnosticProducer diag) { return true; }
     
    public Optional<Type> getResultType () { return resultType; }


}
