package kind.x1.ast;

import kind.x1.misc.SID;
import java.util.Optional;
import java.util.List;

public class ModVisitor 
{
    public interface Visitable { void visit (ModVisitor visitor); }
    
    public void importWild (SID base, Optional<SID> as) { }
    public void importSpecified (SID base, List<String> ids, Optional<SID> as) { }
    public void export (String symbol) { }
    public void define (DefnVisitor.Visitable definition) { }
    public void defineAndExport (DefnVisitor.Visitable definition) { }
}
