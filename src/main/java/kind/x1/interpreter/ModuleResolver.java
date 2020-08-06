package kind.x1.interpreter;

import kind.x1.misc.SID;
import java.util.Optional;

public interface ModuleResolver 
{
    Optional<KindModule> findModule (SID name);
    
    public static final ModuleResolver NULL = new ModuleResolver() {
        public Optional<KindModule> findModule (SID name) { return Optional.empty(); }
    };
}
