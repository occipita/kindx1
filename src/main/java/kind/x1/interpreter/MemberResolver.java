package kind.x1.interpreter;

import kind.x1.interpreter.types.Type;
import kind.x1.Optional;

public interface MemberResolver 
{
    Optional<Type> getMemberType (String name);
}
