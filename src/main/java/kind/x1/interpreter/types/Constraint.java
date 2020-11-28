package kind.x1.interpreter.types;

import kind.x1.Mappers;
import kind.x1.misc.SID;
import kind.x1.interpreter.*;
import kind.x1.interpreter.symbols.Symbol;
import java.util.*;

public class Constraint 
{
    private final SID relation;
    private List<Type> parameters; // FIXME should be called arguments
    private Optional<ConstraintRelation> relationController;

    public Constraint (SID rel, List<Type> params) 
    {
        relation = rel;
        parameters = Collections.unmodifiableList(params);
	relationController = Optional.empty();
    }
    
    public SID getRelation () { return relation; }
    public List<Type> getParameters() { return parameters; }
    /** 
     * Check whether the constraint is satisfied when a specified set of type substitutions
     * is performed on the constraint parameters. The constraint must be resolved before
     * this operation can be performed.
     * @param substitutions a map describing the substitutions to perform; for each 
     *        parameter to the constraint, if it is present as a key in the map then its
     *        associated value is used in its place, otherwise it is used unchanged
     * @return a ConstraintEvidence object of the appropriate type for the relatiom
     *         identified in this constraint if the relation is satisfied (see
     *         implementations of {@link ConstraintRelation} to find the details of
     *         which type should be returned), or an empty optional if either the 
     *         constraint cannot be satisfied or is unresolved.
     */
    public Optional<ConstraintEvidence> checkSatisfied (Map<Type,Type> substitutions)
    {
	return Optional.empty(); // FIXME
    }
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
    public boolean isResolved () { return relationController.isPresent(); }
    /** 
     * Resolve the constraint relation identified in this constraint if not already resolved
     *@returns true on success or false on resolution failure
     */
    public boolean resolve (Resolver resolver)
    {
	if (isResolved()) return true;
	relationController = resolver.resolve (relation).flatMap (Mappers.safeCaster(Symbol.class,ConstraintRelation.class));
	return isResolved();
    }
}
