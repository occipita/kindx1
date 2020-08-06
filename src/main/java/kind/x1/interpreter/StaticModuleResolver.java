package kind.x1.interpreter;

import java.util.Map;
import java.util.HashMap;
import kind.x1.misc.SID;
import java.util.Optional;

public class StaticModuleResolver implements ModuleResolver
{
    private final Map<SID, KindModule> modules = new HashMap<>();
    
    public Optional<KindModule> findModule (SID name) { return Optional.ofNullable(modules.get(name)); }
    public StaticModuleResolver add (SID sid, KindModule mod) { modules.put(sid,mod); return this; }
}
