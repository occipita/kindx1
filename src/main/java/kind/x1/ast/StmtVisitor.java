package kind.x1.ast;

public class StmtVisitor 
{
    public interface Visitable { void visit (StmtVisitor visitor); }
    
    public void ret (ExprVisitor.Visitable expr) { }
    public void retVoid () { }
    public void expression (ExprVisitor.Visitable expr) { } 
    
    public void beginBlock () { }
    public StmtVisitor visitBlockChild () { return null; }
    public void endBlock () { }
}
