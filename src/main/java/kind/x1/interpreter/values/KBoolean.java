package kind.x1.interpreter.values;

import java.util.Optional;
import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.Resolver;

public class KBoolean implements KVal
{
    // FIXME work!
    public Optional<Type> asType () { return Optional.empty(); }
    public Optional<Resolver> getStaticMemberResolver () { return Optional.empty(); }

}
