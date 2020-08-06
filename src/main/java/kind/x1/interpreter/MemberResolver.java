package kind.x1.interpreter;

import kind.x1.interpreter.types.Type;
import java.util.Optional;

public interface MemberResolver 
{
    Optional<Type> getMemberType (String name);
}
