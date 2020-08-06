package kind.x1.interpreter.types;

import kind.x1.misc.SID;
import java.util.List;
import java.util.Collections;

public class Constraint 
{
    private final SID relation;
    private List<Type> parameters; // FIXME should be called arguments

    public Constraint (SID rel, List<Type> params) 
    {
        relation = rel;
        parameters = Collections.unmodifiableList(params);
    }
    
    public SID getRelation () { return relation; }
    public List<Type> getParameters() { return parameters; }
    public String getDescription ()
    {
        StringBuilder b = new StringBuilder(relation.toString()).append("(");
        String sep = "";
        for (Type t : parameters)
        {
            b.append(sep).append(t.getName());
            sep = ", ";
        }
        return b.append(")").toString();
    }
    // FIXME should be able to resolve relation to a ConstraintRelation implementation (eg an interface, superclass checker, 
    // method or property presence verification, etc.). 
}
