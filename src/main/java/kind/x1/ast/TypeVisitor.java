package kind.x1.ast;

import kind.x1.misc.SID;

public class TypeVisitor 
{
    public interface Visitable { void visit(TypeVisitor visitor); }
    
    public void namedType (SID name) { }
    public void expression (ExprVisitor.Visitable expr) { }
    public void beginConstructorCall () { }
    public TypeVisitor visitConstructor () { return IGNORE_TYPE; }
    public TypeVisitor visitConstructorArg () { return IGNORE_TYPE; }
    public void endConstructorCall () { }
    
    public void beginFunction () { }
    /** Called omce for each function parameter */
    public TypeVisitor visitFunctionParameter () { return IGNORE_TYPE; }
    /** Called once if the function returns a value (otherwise function is assumed to be noreturn) */
    public TypeVisitor visitFunctionReturnType () { return IGNORE_TYPE; }
    public void endFunction () { }
    
    public void voidType() { }
    
    public static final TypeVisitor IGNORE_TYPE = new TypeVisitor();
}
