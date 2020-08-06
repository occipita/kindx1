package kind.x1.ast;

import kind.x1.*;
import kind.x1.misc.*;
import java.util.List;
import java.util.Collections;

public class Mod implements ModVisitor.Visitable
{
    public static abstract class Entry
    {
        public abstract void visit (ModVisitor visitor);
    }
    
    public static class Import extends Entry
    {
        private final SID base;
        private final boolean wild;
        private final Optional<List<String>> ids;
        private final Optional<SID> as;
        
        public Import (SID b, boolean w, Optional<List<String>> i, Optional<SID> a)
        { 
            base = b;
            wild = w;
            ids = i.map(Mappers.mapToUnmodifiableList());
            as = a;
        }
        public void visit (ModVisitor visitor)
        {
            if (wild) 
                visitor.importWild (base, as);
            else
                visitor.importSpecified (base, ids.get(), as);
        }
        public String toString()
        {
            return "Mod.Import<"+base+","+wild+","+ids.map(Mappers.MAP_TO_STRING).orElse("-")+","+as.map(Mappers.MAP_TO_STRING).orElse("default")+">";
        }
    }
    
    public static class Export extends Entry
    {
        private final List<String> ids;
        
        public Export(List<String> i) { ids = Collections.unmodifiableList(i); }
        public void visit (ModVisitor visitor)
        {
            for (String id : ids) visitor.export (id);
        }
        public String toString() { return "Mod.Export<"+ids+">"; }
    }
    
    public static class Def extends Entry
    {
        private final Defn defn;
        
        public Def (Defn d) { defn = d; }
        public void visit (ModVisitor visitor)
        {
            visitor.define (defn);
        }
        public String toString() { return "Mod.Def<"+defn+">"; }
    }
    public static class ExportDef extends Entry
    {
        private final Defn defn;
        
        public ExportDef (Defn d) { defn = d; }
        public void visit (ModVisitor visitor)
        {
            visitor.defineAndExport(defn);
        }
        public String toString() { return "Mod.ExportDef<"+defn+">"; }
    }
    
    private final List<Entry> entries;
    
    public Mod(List<Entry> e) { entries = Collections.unmodifiableList(e); }
    public void visit (ModVisitor visitor)
    {
        for (Entry e : entries) e.visit(visitor);
    }
    public String toString () { return "Mod<"+entries+">"; }
    
    static class Handler
    {
        public Import importSpecified (List<String> ids, SID base, Optional<SID> as) { return new Import(base,false,Optional.of(ids),as); } 
        public Import importWild (SID base, Optional<SID> as) { return new Import(base,true,Optional.empty(),as); } 
        public Export export (List<String> ids) { return new Export(ids); }
        public Def def (Defn d) { return new Def(d); }
        public ExportDef exportDef (Defn d) { return new ExportDef(d); }
        public Mod mod (List<Entry>e) { return new Mod(e); }
    }
    
    
}
