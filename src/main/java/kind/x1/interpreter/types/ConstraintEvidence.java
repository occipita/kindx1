package kind.x1.interpreter.types;

/**
 * Interface that supports objects that provide evidence that a type constraint has been satisfied.
 * Depending on the type of constraint, the evidence can be used to perform specific operations that
 * are allowed because the constraint has been satisfied, e.g. if a type parameter T has the constraint
 * <code>kind::hasSuperclass(MyClass)</code> then the ConstraintEvidence supplied for this constraint
 * will have a method that upcasts values of type T to MyClass.
 */
public interface ConstraintEvidence
{
}
