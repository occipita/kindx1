package kind.x1.interpreter.executables;

import kind.x1.interpreter.*;
import kind.x1.*;
import kind.x1.interpreter.types.*;
import java.util.Optional;

public interface Evaluatable 
{
    /** First stage of type inference. Resolve & infer anything that can be inferred
     *  without adding additional type parameters or constraints. Does not produce any
     *  diagnostics if types cannot be resolved or inferred.
     *  @return true if all types within this branch of the tree have been successfully
     *  inferred, false otherwise.
     */
    boolean inferTypesSilently (Resolver resolver, TypeSpec target);
    
    /** Infer and/or check the type of the value produced by evaluatimg this object. 
     *  May add type parameters or constraints to the context. Can produce 
     *  diagnostics if adding parameters or constraints cannot enable determination
     *  of a valid result type. Success should ensure that all types are fully
     *  resolved.
     *  @return true to indicate success or false if type could not be inferred. */
    boolean inferTypes (Resolver resolver, TypeParameterContext context, DiagnosticProducer diag, TypeSpec target);
    
    /** Checks previously inferred types for consistency.
     *  Produces a diagnostic if types are not consistent and no diagnostic was
     *  produced during inference.
     *   @return true iff all types in this branch of the tree are present,
     *           fully resolved and consistent; false otherwise.*/
    boolean checkTypes (DiagnosticProducer diag);
    
    /** Return type of value produced by evaluating this object, if known. Should
     *  always be known and fully resolved if inferTypes has been called ans returned 
     *  true; may be empty if it either has not been called or if it returned false. */
    Optional<Type> getResultType ();
}
