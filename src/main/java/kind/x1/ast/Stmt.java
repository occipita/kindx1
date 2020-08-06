package kind.x1.ast;

import kind.x1.*;
import java.util.List;
import java.util.Collections;
import java.util.Optional;

public abstract class Stmt implements StmtVisitor.Visitable
{
    public static class Exp extends Stmt
    {
        private final Expr expr;
        public Exp(Expr e) { expr = e; }
        public Expr getExpr() { return expr; }
        public String toString() { return "Stmt.Exp<"+expr+">"; } 
        public void visit (StmtVisitor visitor) {visitor.expression(expr); }
    }
    public static class Ret extends Stmt
    {
        private final Optional<Expr> expr;
        public Ret(Expr e) { expr = Optional.of(e); }
        public Ret() { expr = Optional.empty(); }
        public Optional<Expr> getExpr() { return expr; }
        public String toString() { 
            if (expr.isPresent()) return "Stmt.Ret<"+expr.get()+">";
            return "Stmt.Ret<void>"; 
        } 
        public void visit (StmtVisitor visitor) { 
            if (expr.isPresent())
                visitor.ret (expr.get());
            else
                visitor.retVoid();
        }
    }
    public static class Block extends Stmt
    {
        private final List<Stmt> sub;
        public Block (List<Stmt> s) { sub = Collections.unmodifiableList(s); }
        public List<Stmt> getSub () { return sub; }
        public String toString() { return "Stmt.Block<"+sub+">"; }
        public void visit (StmtVisitor visitor) { 
            visitor.beginBlock();
            for (Stmt s : sub)
            {
                StmtVisitor   vs = visitor.visitBlockChild();
                if (vs != null) s.visit (vs);
            }
            visitor.endBlock();
        }
    }
    public static class Null extends Stmt
    {
        public String toString() { return "Stmt.Null"; }
        public void visit (StmtVisitor visitor) { /* FIXME */ }
    }
    public static class While extends Stmt
    {
        private final Expr cond;
        private final Stmt stmt;
        public While(Expr e, Stmt s) { cond = e; stmt = s; }
        public Expr getCond() { return cond; }
        public Stmt getStmt() { return stmt; }
        public String toString() { return "Stmt.While<"+cond+","+stmt+">"; } 
        public void visit (StmtVisitor visitor) { /* FIXME */ }
    }
    public static class If extends Stmt
    {
        private final Expr cond;
        private final Stmt primary;
        private final Optional<Stmt> alternative;
        public If(Expr e, Stmt s) { cond = e; primary = s; alternative = Optional.empty(); }
        public If(Expr e, Stmt s1, Optional<Stmt> s2) { cond = e; primary = s1; alternative = s2; }
        public Expr getCond() { return cond; }
        public Stmt getPrimary() { return primary; }
        public Optional<Stmt> getAlternative() { return alternative; }
        public String toString() { return "Stmt.If<"+cond+","+primary+","+(alternative.isPresent()?alternative.get().toString():"none")+">"; } 
        public void visit (StmtVisitor visitor) { /* FIXME */ }
    }
    public static class VarDecl extends Stmt
    {
        private final List<String> vars;
        private final Optional<Type> type;
        private final Optional<Expr> expr;
        
        public VarDecl (List<String> v, Optional<Type> t, Optional<Expr> e) { vars = Collections.unmodifiableList(v); type = t; expr = e; }
        public List<String> getVars () { return vars; }
        public Optional<Type> getType() { return type; }
        public Optional<Expr> getExpr() { return expr; }
        public String toString() {
            return "Stmt.VarDecl<"+vars+","+
                (type.isPresent() ? type.get().toString() : "inferred") +","+
                (expr.isPresent() ? expr.get().toString() : "default") +">";
        }
        public void visit (StmtVisitor visitor) { /* FIXME */ }
    }
    public static class Handler
    {
        public Exp expr(Expr e) { return new Exp(e); }
        public Ret ret(Expr e) { return new Ret(e); }
        public Ret ret() { return new Ret(); }
        public Block block(List<Stmt> s) { return new Block(s); }   
        public Null nullStmt() { return new Null(); }
        // nb two ways to build vardecls, but at least one of type or expr must be prese
        public VarDecl varDecl (List<String> vars, Type type, Optional<Expr> expr) {
            return new VarDecl(vars, Optional.of(type), expr);
        }
        public VarDecl varDecl (List<String> vars, Expr expr) {
            return new VarDecl(vars, Optional.empty(), Optional.of(expr));
        }
        public While whileStmt (Expr e, Stmt s) { return new While(e,s); }
        public If ifStmt (Expr e, Stmt s) { return new If(e,s); }
        public If ifStmt (Expr e, Stmt s1, Optional<Stmt> s2) { return new If(e,s1,s2); }
    }
}
