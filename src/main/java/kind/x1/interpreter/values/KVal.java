package kind.x1.interpreter.values;

import java.util.Optional;
import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.Resolver;

public interface KVal 
{
    Optional<Type> asType ();
    Optional<Resolver> getStaticMemberResolver ();
}