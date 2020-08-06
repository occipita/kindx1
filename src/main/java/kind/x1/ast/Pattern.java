package kind.x1.ast;

import kind.x1.*;

public abstract class Pattern implements PatternVisitor.Visitable
{
    public static class Named extends Pattern
    {
        private final String id;
        private final Optional<Type> type;
        public Named(String i, Optional<Type> t) { id = i; type = t; }
        public String getId() { return id; }
        public Optional<Type> getType() { return type; }
        public String toString () { return "Pattern.Named<"+id+","+(type.isPresent()?type.get().toString():"inferred")+">"; }
        public void visit (PatternVisitor v) {
            TypeVisitor tv = v.beginNamed(id);
            if (tv != null && type.isPresent()) type.get().visit(tv);
            v.endNamed();
        }
    }
   
    public static class Handler
    {
        public Named named (Token id, Optional<Type> type) { return new Named(id.text(),type); } 
    }
}
