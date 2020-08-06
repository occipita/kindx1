package kind.x1.ast;

public class PatternVisitor 
{
    public interface Visitable { void visit (PatternVisitor v); }
    
    public TypeVisitor beginNamed (String name) { return null; }
    public void endNamed () { }   
}
