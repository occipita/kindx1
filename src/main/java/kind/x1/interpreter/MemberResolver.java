package kind.x1.interpreter;

import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.values.KVal;
import java.util.Optional;

public interface MemberResolver 
{
    Optional<Type> getMemberType (String name);
    Optional<KVal> getMemberValue (KVal object, String name);
}
