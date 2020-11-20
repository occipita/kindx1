package kind.x1.interpreter;

import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.values.KVal;
import kind.x1.interpreter.values.KCallable;;
import java.util.Optional;

public interface MemberResolver 
{
    /** Returns the type of the member with a given name, if any */
    Optional<Type> getMemberType (String name);
    /** 
     * Returns the value of the member of a specified object of this type (or any subtype) 
     * with the given name, if any.
     */
    Optional<KVal> getMemberValue (KVal object, String name);
    /** 
     * Returns the type of the member operator (which must always be a function type
     * with a single argument) with a given name, if any.
     */
    Optional<Type> getMemberOperatorType (String name);
    /**
     * Binds and returns the implementation of the member operator with the given name
     *@param object  an object of the type represented by this object or a subtype
     *@param name    the name of the operator to be bound and returned
     *@returns a callable object that implements the specified operator, bound to
     *         <code>object</code> as the left hand argument, or Optional.none() if
     *         not available.
     */
    Optional<KCallable> getMemberOperator (KVal object, String name);
}
