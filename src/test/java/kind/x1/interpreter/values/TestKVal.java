package kind.x1.interpreter.values;

import java.util.Optional;
import kind.x1.interpreter.types.Type;
import kind.x1.interpreter.Resolver;

public class TestKVal implements KVal
{
    public Optional<Type> asType () { return Optional.empty (); }
    public Optional<Resolver> getStaticMemberResolver () { return Optional.empty(); }    
}
