package kind.x1.ast;

import kind.x1.*;
import java.util.*;
import static kind.x1.TokenType.*;
import kind.x1.misc.SID;

public abstract class Expr implements ExprVisitor.Visitable
{
    public static class VariableRef extends Expr
    {
        private final SID id;
        
        public VariableRef(SID i) { this.id = i; }
        public SID getId() { return id; }
        public String toString() { return "Expr.VariableRef<"+id+">"; }
        public void visit (ExprVisitor v) { v.variableRef(id); }
    }
    public static class IntLiteral extends Expr
    {
        private final String val;
        private final List<String> flags;
        
        public IntLiteral(String i, List<String> f) { this.val = i; this.flags = Collections.unmodifiableList(f); }
        public String getVal() { return val; }
        public List<String> getFlags() { return flags; }
        public String toString() { return "Expr.IntLiteral<"+val+","+flags+">"; }
        public void visit (ExprVisitor v) {
            ExprVisitor.LiteralFlagVisitor fv = v.intLiteral(val);
            for (String f : flags) fv.literalFlag(f); 
        }
    }
    public static class FloatLiteral extends Expr
    {
        private final String val;
        private final List<String> flags;
        
        public FloatLiteral(String i, List<String> f) { this.val = i; this.flags = Collections.unmodifiableList(f); }
        public String getVal() { return val; }
        public List<String> getFlags() { return flags; }
        public String toString() { return "Expr.FloatLiteral<"+val+","+flags+">"; }
        public void visit (ExprVisitor v) {
            ExprVisitor.LiteralFlagVisitor fv = v.floatLiteral(val);
            for (String f : flags) fv.literalFlag(f); 
	}
    }
    public static class StringLiteral extends Expr
    {
        private final String val;
        private final List<String> flags;
        
        public StringLiteral(String i, List<String> f) { this.val = i; this.flags = Collections.unmodifiableList(f); }
        public String getVal() { return val; }
        public List<String> getFlags() { return flags; }
        public String toString() { return "Expr.StringLiteral<"+val+","+flags+">"; }
        public void visit (ExprVisitor v) {
	    ExprVisitor.LiteralFlagVisitor fv = v.stringLiteral(val);
	    for (String f : flags) fv.literalFlag(f); 
	}
    }
    public static class RAssoc extends Expr
    {
        private final List<Expr> subExprs;
        private final List<String> ops;
        
        private RAssoc (List<Expr> s, List<String> o) { 
            subExprs = Collections.unmodifiableList(s); 
            ops = Collections.unmodifiableList(o);
        }
        public List<Expr> getSubExprs() { return subExprs; }
        public List<String> getOps() { return ops; }
        public String toString() { return "Expr.RAssoc<"+subExprs+","+ops+">"; }
        public void visit (ExprVisitor v) { 
            v.beginOperatorChainRight();
            for (int i = 0; i < ops.size(); i++) {
                subExprs.get(i).visit(v.operand());
                v.operator(ops.get(i));
            }
            v.endOperatorChain();
        }
    }
    public static class LAssoc extends Expr
    {
        private final List<Expr> subExprs;
        private final List<String> ops;
        
        private LAssoc (List<Expr> s, List<String> o) { 
            subExprs = Collections.unmodifiableList(s); 
            ops = Collections.unmodifiableList(o);
        }
        public List<Expr> getSubExprs() { return subExprs; }
        public List<String> getOps() { return ops; }
        public String toString() { return "Expr.LAssoc<"+subExprs+","+ops+">"; }
        public void visit (ExprVisitor v) { 
            v.beginOperatorChainLeft();
            for (int i = 0; i < ops.size(); i++) {
                subExprs.get(i).visit(v.operand());
                v.operator(ops.get(i));
            }
            v.endOperatorChain();
        }
    }
    public static class Apply extends Expr
    {
        private final Expr subExpr;
        private final List<Applicable> ops;
        
        private Apply (Expr s, List<Applicable> o) { 
            subExpr = s; 
            ops = Collections.unmodifiableList(o);
        }
        public Expr getSubExpr() { return subExpr; }
        public List<Applicable> getOps() { return ops; }
        public String toString() { return "Expr.Apply<"+subExpr+","+ops+">"; }
        public void visit (ExprVisitor v) { 
            subExpr.visit (v);
            for (Applicable a : ops)
                a.visit(v);
        }
    }
    public interface Applicable
    {
        public void visit (ExprVisitor v);
    }
    public static class Dot implements Applicable
    {
        private final String id;
        public Dot (String i) { id = i; }
        public String getId() { return id; }
        public String toString() { return "Expr.Dot<"+id+">"; }
        public void visit (ExprVisitor v) { v.applyDot(id); }
    }
    public static class Index implements Applicable
    {
        private final List<Expr> sub;
        public Index(List<Expr> i) { sub = Collections.unmodifiableList(i); }
        public List<Expr> getSub() { return sub;}
        public String toString() { return "Expr.Index<"+sub+">"; }
        public void visit (ExprVisitor v) { }
    }
    public static class FnCall implements Applicable
    {
        private final List<Expr> args;
        public FnCall(List<Expr> i) { args = Collections.unmodifiableList(i); }
        public FnCall() { args = Collections.emptyList(); }
        public List<Expr> getArgs() { return args;}
        public String toString() { return "Expr.FnCall<"+args+">"; }
        public void visit (ExprVisitor v) { 
            v.beginApplyFnCall ();
            for (Expr arg : args) arg.visit(v.fnCallArgument ());
            v.endApplyFnCall ();
        }
    }
    public static class Op implements Applicable
    {
        private final String id;
        public Op (String i) { id = i; }
        public String getId() { return id; }
        public String toString() { return "Expr.Op<"+id+">"; }
        public void visit (ExprVisitor v) { v.applyUnaryOp (id); }
    }
    public static class Lambda extends Expr
    {
        private final List<Defn.FnBody> bodies;
        public Lambda(List<Defn.FnBody> b) { bodies = Collections.unmodifiableList(b); }
        public List<Defn.FnBody> getBodies(){ return bodies; }
        public void visit (ExprVisitor v) { }
        public String toString() { return "Expr.Lambda<"+bodies+">"; }
    }
    public static class Handler
    {
        
        public VariableRef variableRef(SID id) { return new VariableRef(id); }
        public IntLiteral intLiteral(Token literal) { return new IntLiteral(literal.text(),Collections.emptyList()); }
        public IntLiteral intLiteral(Token literal, List<String> flags) { return new IntLiteral(literal.text(),flags); }
        public FloatLiteral floatLiteral(Token literal) { return new FloatLiteral(literal.text(),Collections.emptyList()); }
        public FloatLiteral floatLiteral(Token literal, List<String> flags) { return new FloatLiteral(literal.text(),flags); }
        public StringLiteral stringLiteral(Token literal) { return new StringLiteral(literal.text(),Collections.emptyList()); }
        public StringLiteral stringLiteral(Token literal, List<String> flags) { return new StringLiteral(literal.text(),flags); }
        public List<String> flagList(List<Token> ids) {
            List<String> res = new ArrayList<>(ids.size());
            for (Token t : ids) res.add(t.text());
            return res;
        }
        public Expr rAssoc (List<Expr> sub, List<Token> ops) {
            if (sub.size() != ops.size() + 1) 
                throw new IllegalArgumentException("cannot build expression with sub: " + sub + " and ops: " + ops);
            if (sub.size() == 1) return sub.get(0);
            List<String> opstrs = new ArrayList<>(ops.size());
            for (Token t : ops) opstrs.add(t.text());
            
            return new RAssoc(sub, opstrs);
        }
        public Expr lAssoc (List<Expr> sub, List<Token> ops) {
            if (sub.size() != ops.size() + 1) 
                throw new IllegalArgumentException("cannot build expression with sub: " + sub + " and ops: " + ops);
            if (sub.size() == 1) return sub.get(0);
            List<String> opstrs = new ArrayList<>(ops.size());
            for (Token t : ops) opstrs.add(t.text());
            
            return new LAssoc(sub, opstrs);
        }
        public Expr apply (List<Applicable> pre, Expr sub, List<Applicable> post) {
            if (pre.isEmpty() && post.isEmpty()) return sub;
            List<Applicable> ops = new ArrayList<>(pre.size()+post.size());
            ops.addAll(pre);
            ops.addAll(post);
            return new Apply(sub,ops);
        }
        public LinkedList<Applicable> dot(Token id, LinkedList<Applicable> t)
        {
            t.addFirst (new Dot(id.text()));
            return t;
        }
        public LinkedList<Applicable> index(List<Expr> sub, LinkedList<Applicable> t)
        {
            t.addFirst (new Index(sub));
            return t;
        }
        public LinkedList<Applicable> fnCall(List<Expr> sub, LinkedList<Applicable> t)
        {
            t.addFirst (new FnCall(sub));
            return t;
        }
        public LinkedList<Applicable> emptyFnCall(LinkedList<Applicable> t)
        {
            t.addFirst (new FnCall());
            return t;
        }
        public LinkedList<Applicable> postOp(Token op, LinkedList<Applicable> t)
        {
            String opstr;
            switch(op.type()) {
                    case DOUBLEPLUS: opstr = "++post"; break;
                    case DOUBLEMINUS: opstr = "--post"; break;
                    default: opstr = op.text();
            }           
            t.addFirst (new Op(opstr));
            return t;
        }
        public LinkedList<Applicable> preOp(Token op, LinkedList<Applicable> t)
        {
            String opstr;
            switch(op.type()) {
                    case DOUBLEPLUS: opstr = "++pre"; break;
                    case DOUBLEMINUS: opstr = "--pre"; break;
                    default: opstr = op.text();
            }           
            t.add (new Op(opstr));
            return t;
        }
        public Lambda lambda(List<Defn.FnBody> b) { return new Lambda(b); }
    }
}
