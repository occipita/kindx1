package kind.x1.interpreter.executables;

import kind.x1.interpreter.types.*;
import kind.x1.interpreter.values.KVal;
import kind.x1.interpreter.*;
import kind.x1.*;
import kind.x1.misc.SID;
import java.util.Arrays;
import java.util.Optional;

public class DotApplication implements Evaluatable
{
    private Evaluatable subExpr;
    private String id;
    private Optional<Type> resultType = Optional.empty();
        
    public DotApplication(Evaluatable sub, String id) { subExpr = sub; this.id = id; }
    public Evaluatable getSubExpr () { return subExpr; }
    public String getId () { return id; }

    public boolean inferTypesSilently (Resolver resolver, TypeSpec target) 
    { 
        if (resultType.isPresent()) return true;
        if (!subExpr.inferTypesSilently(resolver, TypeSpec.UNSPECIFIED)) return false;
        resolveMemberType();
        return resultType.isPresent(); 
    }
    /** precondition - subExpr has returned true from an infer* method
     *  @return type of left hand side */ 
    private Type resolveMemberType ()
    {
        Type lhs = subExpr.getResultType().get();
        if (lhs instanceof MemberResolver) resultType = ((MemberResolver)lhs).getMemberType (id);
        return lhs;
    }
    public boolean inferTypes (Resolver resolver, TypeParameterContext context, DiagnosticProducer diag, TypeSpec expected) 
    { 
        if (resultType.isPresent()) return true;
        if (!subExpr.inferTypes(resolver, context, diag, TypeSpec.UNSPECIFIED)) return false;
        Type lhs = resolveMemberType ();
        if (!resultType.isPresent ()) {
            if (!(lhs instanceof MemberResolver)) {
                diag.error ("Type '"+lhs.getName()+"' does not support the '.' operator");
                return false;
            }
            // if type is an implicit parameter, we may be able to infer a constraint:
            if (lhs instanceof TypeParameterContext.Parameter && ((TypeParameterContext.Parameter)lhs).isImplicit())
            {
                Type rt;
                switch (expected.getMode())
                {
                    case UNSPECIFIED:
                        rt = context.addImplicit();
                        break;
                    default:
                        rt = expected.getType();
                }
                ((TypeParameterContext.Parameter)lhs).addInferredConstraint (
                    new Constraint(
                        SID.from("kind::core::propertyReadable").append(id),
                            Arrays.asList(
                                lhs,
                                rt)));
                resultType = Optional.of(rt);
                return true;
                        
            }
            diag.error ("Type '"+lhs.getName()+"' does not have a definition for '"+id+"'");
            return false;
        }
        return true; 
    }
    public boolean checkTypes (DiagnosticProducer diag) { return true; }
     
    public Optional<Type> getResultType () { return resultType; }

}
