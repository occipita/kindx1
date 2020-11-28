package kind.x1.interpreter.types;

import java.util.*;
import kind.x1.interpreter.symbols.*;
import kind.x1.interpreter.values.KVal;

/**
 * Identifies a relation that may hold for one or more types and
 * which can be tested for via a type constraint, typically such
 * that some operation(s) can be performed on values of the given
 * types if and only if the relation holds on them. Successfully
 * testing for the existence of a relation produces evidence
 * which can then be used as a proxy to perform whatever operations
 * were tested for without needing to know the types directly.
 * <p>
 * Subclasses of this class provide the specific implementation
 * of the tests in question; each subclass has its own subclass of
 * {@link ConstraintEvidence} that it produces as a proxy to
 * perform the relevant operations.
 */ 
public abstract class ConstraintRelation extends Symbol
{
    public ConstraintRelation (String id) { super(id); }

    /** 
     * Check whether the relation holds for a given list of types.
     * @param types the types to check, which should be fully resolved 
     *              and concrete (ie not type parameters)
     * @returns an Optional containing appropriate evidence for this
     *          relation type if the relation holds, or Optional.none()
     *          if it does not.
     */
    public abstract Optional<ConstraintEvidence> check (List<Type> types);
}
