package kind.x1.ast;

import kind.x1.misc.SID;

/** Expression visitor. Each expression is created by some operation
 * that generates a result (which may involve using subexpressions
 * which are created through a new ExprVisitor returned by a method this
 * visitor). Then, zero or more tranformations are applied (either
 * through single methods with name "applyXXX" or grouped with
 * "beginApplyXXX", some other related calls, then "endApplyXXX";
 * these 'apply' operations modify the result already calculated 
 * in the current visitor. 
 */
public class ExprVisitor 
{
    public interface Visitable { void visit (ExprVisitor visitor); }
    public interface LiteralFlagVisitor 
    { 
        void literalFlag (String value);
    }
    
    public LiteralFlagVisitor intLiteral (String text) 
    { 
        return IGNORE_LITERAL_FLAGS;
    }
    public LiteralFlagVisitor stringLiteral (String text) 
    { 
        return IGNORE_LITERAL_FLAGS;
    }
    public LiteralFlagVisitor floatLiteral (String text) 
    { 
        return IGNORE_LITERAL_FLAGS;
    }
    
    public void variableRef (SID id) { }
    
    public void beginOperatorChainRight () { }
    public void beginOperatorChainLeft () { }
    public ExprVisitor operand () { return IGNORE_SUBEXPRESSION; }
    public void operator (String operator) { }
    public void endOperatorChain () { }
    
    public void applyDot (String id) { }
    
    public void beginApplyFnCall () { }
    public ExprVisitor fnCallArgument () { return IGNORE_SUBEXPRESSION; }
    public void endApplyFnCall () { }
    
    public void applyUnaryOp (String id) { }
    
    public static final LiteralFlagVisitor IGNORE_LITERAL_FLAGS = new LiteralFlagVisitor() {
            public void literalFlag (String value) { }
        };
    public static final ExprVisitor IGNORE_SUBEXPRESSION = new ExprVisitor();
}
