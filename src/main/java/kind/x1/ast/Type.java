package kind.x1.ast;

import java.util.Collections;
import java.util.List;
import kind.x1.*;
import kind.x1.misc.*;

public abstract class Type implements TypeVisitor.Visitable
{
    public static class Exp extends Type
    {
        private final Expr expr;
        public Exp(Expr e) { expr = e; }
        public Expr getExpr() { return expr; }
        public String toString() { return "Type.Exp<"+expr+">"; } 
        public void visit (TypeVisitor v) { v.expression(expr); }
    }
    public static class NamedType extends Type
    {
        private final SID id;
        public NamedType(SID i) { id = i; }
        public SID getId() { return id; }
        public String toString () { return "Type.NamedType<"+id+">"; }
        public void visit (TypeVisitor v) { v.namedType(id); }
    }
    public static class Cons extends Type
    {
        private final Type constructor;
        private final List<Type> args;
        public Cons(Type c, List<Type> a) { 
            constructor = c;
            args = Collections.unmodifiableList(a);
        }
        public Type getConstructor() { return constructor; }
        public List<Type> getArgs() { return args; }
        public String toString () { return "Type.Cons<"+constructor+","+args+">"; }
        public void visit (TypeVisitor v) 
        {
            v.beginConstructorCall();
            TypeVisitor vc = v.visitConstructor(); if (vc != null) constructor.visit(vc);
            for (Type a : args) {
                TypeVisitor va = v.visitConstructorArg(); 
                if (va != null) a.visit(va);
            }
            v.endConstructorCall();
            
        }
    }
    
    
    public static class Constraint
    {
        private final SID relation;
        private final List<Type> args;
        public Constraint(SID r, List<Type> a) { 
            relation = r;
            args = Collections.unmodifiableList(a);
        }
        public SID getRelation() { return relation; }
        public List<Type> getArgs() { return args; }
        public String toString () { return "Type.Constraint<"+relation+","+args+">"; }
        public void visit (TypeConstraintVisitor v) {
            v.relation(relation);
            for (Type t : args) v.parameter(t);
        }
    }
    
    public static class Handler
    {
        public Exp expr (Expr e) { return new Exp(e); }
        public NamedType namedType (SID id) { return new NamedType(id); }
        public Cons cons (Type c, List<Type> a) { return new Cons(c,a); }
        public Constraint namedConstraint (SID id, List<Type> args) { return new Constraint(id,args); }
    }
}
