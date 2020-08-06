package kind.x1.ast;

import kind.x1.*;
import static kind.x1.TokenType.*;
import java.util.List;
import java.util.Collections;

public abstract class Defn implements DefnVisitor.Visitable
{
    public static class Fn extends Defn
    {
        private final String name;
        private final Optional<Type> returnType;
        private final FnBody body;
        
        public Fn (String n, Optional<Type> t, FnBody b) { name = n; returnType = t; body = b; }
        public String getName () { return name; }
        public Optional<Type> getReturnType() { return returnType; }
        public void visit (DefnVisitor visitor) {
            DefnVisitor fv = visitor.beginFunction(name);
            if (fv != null) {
                body.visit(fv);
                if (returnType.isPresent()) fv.returnType(returnType.get());
                fv.endFunctionBody();
            }
            visitor.endFunction();
        }
        public String toString() {
            return "Defn.Fn<"+name+","+
                (returnType.isPresent() ? returnType.get().toString() : "inferred") +","+
                body +">";
        }
    }
    public static class Op extends Defn
    {
        private final String name;
        private final Optional<Type> returnType;
        private final FnBody body;
        
        public Op (String n, Optional<Type> t, FnBody b) { name = n; returnType = t; body = b; }
        public String getName () { return name; }
        public Optional<Type> getReturnType() { return returnType; }
        public void visit (DefnVisitor visitor) {
            DefnVisitor fv = visitor.beginOperatorFunction(name);
            if (fv != null) {
                body.visit(fv);
                if (returnType.isPresent()) fv.returnType(returnType.get());
                fv.endFunctionBody();
            }
            visitor.endFunction();
        }
        public String toString() {
            return "Defn.Op<'"+name+"',"+
                (returnType.isPresent() ? returnType.get().toString() : "inferred") +","+
                body +">";
        }
    }
    public static class AbstractFn extends Defn
    {
        private final String name;
        private final Type returnType;
        private final List<Pattern> pattern;
        
        public AbstractFn (String n, Type t, List<Pattern> p) { name = n; returnType = t; pattern = Collections.unmodifiableList(p); }
        public String getName () { return name; }
        public Type getReturnType() { return returnType; }
        public void visit (DefnVisitor visitor) {
            DefnVisitor fv = visitor.beginFunction(name);
            if (fv != null) {
                fv.beginAbstractBody();
                for (Pattern p : pattern) fv.parameterPattern (p);
                fv.returnType(returnType);
                fv.endFunctionBody();
            }
            visitor.endFunction();
        }
        public String toString() {
            return "Defn.AbstractFn<"+name+","+returnType +","+pattern+">";
        }
    }
    public static class AbstractOp extends Defn
    {
        private final String name;
        private final Type returnType;
        private final List<Pattern> pattern;
        
        public AbstractOp (String n, Type t, List<Pattern> p) { name = n; returnType = t; pattern = Collections.unmodifiableList(p); }
        public String getName () { return name; }
        public Type getReturnType() { return returnType; }
        public void visit (DefnVisitor visitor) {
            DefnVisitor fv = visitor.beginOperatorFunction(name);
            if (fv != null) {
                fv.beginAbstractBody();
                for (Pattern p : pattern) fv.parameterPattern (p);
                fv.returnType(returnType);
                fv.endFunctionBody();
            }
            visitor.endFunction();
        }
        public String toString() {
            return "Defn.AbstractOp<'"+name+"',"+returnType +","+pattern+">";
        }
    }
    public static class ClassDef extends Defn
    {
        private final String name;
        private final List<String> parameters;
        private final List<Type.Constraint> constraints;
        private final Optional<String> metaclass;
        private final List<Type> superclasses;
        private final List<Defn> members;
        
        public ClassDef (String n, List<String> pa, List<Type.Constraint> c, Optional<String> m, List<Type> s, List<Defn> me) 
        { 
            name = n;
            metaclass = m; 
            parameters = Collections.unmodifiableList(pa);
            constraints = Collections.unmodifiableList(c);
            superclasses = Collections.unmodifiableList(s);
            members= Collections.unmodifiableList(me);
        }
        public String getName() { return name; }
        public Optional<String> getMetaclass() { return metaclass; }
        public void visit (DefnVisitor visitor) {
            // FIXME
        }
        public String toString () {
            return "Defn.ClassDef<"+name+","+parameters+","+constraints+","+
                metaclass.orElse("default")+","+
                superclasses+","+
                members+">";
        }        
    }
    public static class InterfaceDef extends Defn
    {
        private final String name;
        private final Optional<List<String>> placeholder;
        private final List<String> parameters;
        private final List<Type.Constraint> constraints;
        private final List<Type> superinterfaces; // FIXME need to be able to supply parameters
        private final List<Defn> members;
        
        public InterfaceDef (String n, List<String> pa, List<Type.Constraint> c, List<Type> si, Optional<List<String>> pl, List<Defn> me) 
        { 
            name = n;
            placeholder = pl; 
            parameters = Collections.unmodifiableList(pa);
            constraints = Collections.unmodifiableList(c);
            superinterfaces = Collections.unmodifiableList(si);
            members= Collections.unmodifiableList(me);
        }
        public String getName() { return name; }
        public void visit (DefnVisitor visitor) {
            DefnVisitor s = visitor.beginInterface(name);
            if (s != null) {
                for (String p : parameters) s.interfaceParameter(p); 
                for (Type.Constraint c : constraints) s.interfaceConstraint(c); 
                for (Type si : superinterfaces) s.interfaceSuperinterface(si);
                if (placeholder.isPresent())
                    s.interfacePlaceholder(placeholder.get()); 
                for (Defn d : members)
                {
                    DefnVisitor mv = s.visitInterfaceMember();
                    if (mv != null) d.visit(mv);
                }     
            }
            visitor.endInterface();
        }
        public String toString () {
            return "Defn.InterfaceDef<"+name+","+parameters+","+constraints+","+superinterfaces+","+
                placeholder.map(Mappers.MAP_TO_STRING).orElse("none")+","+
                members+">";
        }        
    }
    public static class ImplementationDef extends Defn
    {
        private final String ifName;
        private final List<Type> ifArgs;
        private final Optional<List<Type>> implementors;
        private final List<Defn> members;
        
        public ImplementationDef (String n, List<Type> a, Optional<List<Type>> i, List<Defn> me) 
        { 
            ifName = n;
            ifArgs = Collections.unmodifiableList(a);
            implementors = i.map(Mappers.mapToUnmodifiableList());
            members= Collections.unmodifiableList(me);
        }
        public void visit (DefnVisitor visitor) {
            // FIXME
        }
        public String toString () {
            return "Defn.ImplementationDef<"+ifName+","+ifArgs+","+
                implementors.map(Mappers.MAP_TO_STRING).orElse("self")+","+
                members+">";
        }        
    }
    public static class Property extends Defn
    {
        private final String name;
        private final Optional<Type> type;
        private final List<PropertyAccessor> accessors;
        
        public Property (String n, Optional<Type> t, List<PropertyAccessor> p) { name = n; type = t; accessors = Collections.unmodifiableList(p); }
        public String getName () { return name; }
        public Optional<Type> getType() { return type; }
        public void visit (DefnVisitor visitor) {
            DefnVisitor av = visitor.property(name, type);
            if (av != null)
                for (PropertyAccessor pa : accessors) pa.visit(av);
            visitor.endProperty();
        }
        public String toString() {
            return "Defn.Property<"+name+","+
                (type.isPresent() ? type.get().toString() : "inferred")
                +","+accessors+">";
        }
    }
    public static final int KW_ATTR_MIN = KW_PUBLIC;
    public static final int PUBLIC = 1 << (KW_PUBLIC - KW_ATTR_MIN);
    public static final int PRIVATE = 1 << (KW_PRIVATE - KW_ATTR_MIN);
    public static final int PROTECTED = 1 << (KW_PROTECTED - KW_ATTR_MIN);
    public static final int ABSTRACT = 1 << (KW_ABSTRACT - KW_ATTR_MIN);
    public static final int STATIC = 1 << (KW_STATIC - KW_ATTR_MIN);
    public static final int FINAL = 1 << (KW_FINAL - KW_ATTR_MIN);
     
    public static class AttrList
    {
        private int bits;
        public void add (Token t) { bits |= 1 << (t.type() - KW_ATTR_MIN); }
        public String toString()
        {
            StringBuilder r= new StringBuilder();
            String s = "";
            if ((bits&PUBLIC) != 0) { r.append(s).append("public"); s=" "; }
            if ((bits&PRIVATE) != 0) { r.append(s).append("private"); s=" "; }
            if ((bits&PROTECTED) != 0) { r.append(s).append("protected"); s=" "; }
            if ((bits&ABSTRACT) != 0) { r.append(s).append("abstract"); s=" "; }
            if ((bits&STATIC) != 0) { r.append(s).append("static"); s=" "; }
            if ((bits&FINAL) != 0) { r.append(s).append("final"); s=" "; }
            return r.toString();
        }
    }
    public static class Attr extends Defn
    {
        private final AttrList attrList;
        private final Defn defn;
        
        public Attr (AttrList al, Defn d) { attrList = al; defn= d; }
        public AttrList getAttrList () { return attrList; }
        public Defn getDefn() { return defn; }
        
        public void visit (DefnVisitor visitor) {
            // FIXME
        }

        public String toString() {
            return "Defn.Attr<"+attrList+","+defn +">";
        }
    }
    public static class ForAll extends Defn
    {
        private final List<String> parameters;
        private final List<Type.Constraint> constraints;
        private final Defn defn;
        
        public ForAll (List<String> p, List<Type.Constraint> c, Defn d) 
        { 
            parameters = Collections.unmodifiableList(p);
            constraints = Collections.unmodifiableList(c);
            defn= d; 
        }
        public Defn getDefn() { return defn; }
        public void visit (DefnVisitor visitor) {
            visitor.beginForAll();
            for (String p : parameters) visitor.typeParameter(p);
            for (Type.Constraint c : constraints) visitor.typeConstraint(c);
            defn.visit(visitor);
        }
        public String toString() {
            return "Defn.ForAll<"+parameters+","+constraints+","+defn +">";
        }
    }

    public static class FnBody
    {
        private final List<Pattern> pattern;
        private final Stmt stmt;
        public FnBody (List<Pattern> p, Expr e)
        {
            pattern = Collections.unmodifiableList(p);
            stmt = new Stmt.Ret(e);
        }
        public FnBody (List<Pattern> p, Stmt s)
        {
            pattern = Collections.unmodifiableList(p);
            stmt = s;
        }
        public List<Pattern> getPattern() { return pattern; }
        public Stmt getStmt() { return stmt; }
        public String toString() { return "Defn.FnBody<"+pattern+","+stmt+">"; }
        public void visit (DefnVisitor fv)
        {
            fv.beginImplementationBody();
            for (Pattern p : pattern) fv.parameterPattern (p);
            fv.bodyImplementation (stmt);    
        }
    }
    
    public enum AccessorType {
        // overridable accessors 
        GET, SET, INIT,
        // permanently inherited event handlers
        BEFORE_SET, AFTER_SET, BEFORE_GET, AFTER_INIT;
    }
    public static class PropertyAccessor
    {
        private final AccessorType type;
        private final Stmt stmt;
        public PropertyAccessor (AccessorType t, Expr e)
        {
            type = t;
            stmt = new Stmt.Ret(e);
        }
        public PropertyAccessor (AccessorType t, Stmt s)
        {
            type = t;
            stmt = s;
        }
        public void visit (DefnVisitor visitor) { visitor.propertyAccessor (type, stmt); }
        public Stmt getStmt() { return stmt; }
        public String toString() { return "Defn.PropertyAccessor<"+type+","+stmt+">"; }
    }
    public static class Handler
    {
        public FnBody fnBody (List<Pattern> p, Expr e) { return new FnBody(p,e); }
        public FnBody fnBody (List<Pattern> p, Stmt s) { return new FnBody(p,s); }
        public Fn fn (Token id, List<Pattern> args, Optional<Type> rtype, Stmt stmt)
        {
            return new Fn(id.text(), rtype, fnBody(args,stmt));
        }
        public Fn fn (Token id, List<Pattern> args, Optional<Type> rtype, Expr e)
        {
            return new Fn(id.text(), rtype, fnBody(args,e));
        }
        public Op op (Token op, List<Pattern> args, Optional<Type> rtype, Stmt stmt)
        {
            return new Op(op.text(), rtype, fnBody(args,stmt));
        }
        public Op op (Token op, List<Pattern> args, Optional<Type> rtype, Expr e)
        {
            return new Op(op.text(), rtype, fnBody(args,e));
        }
        public AbstractFn abstractFn (Token id, List<Pattern> args, Type rtype)
        {
            return new AbstractFn(id.text(), rtype, args);
        }
        public AbstractOp abstractOp (Token op, List<Pattern> args, Type rtype)
        {
            return new AbstractOp(op.text(), rtype, args);
        }
        public ClassDef classDef (Token id, Optional<Pair<List<String>, List<Type.Constraint>>> ao, Optional<Token> meta, Optional<List<Type>> sup, List<Defn> mem)
        {
            Pair<List<String>, List<Type.Constraint>> args = ao.orElse(new Pair<>(Collections.emptyList(),Collections.emptyList()));
            return new ClassDef (id.text(), args.v1, args.v2, meta.map(new Mapper<Token,String>(){ 
                    public String map(Token t) { return t.text(); }
                }), sup.orElse(Collections.emptyList()), mem);
        }
        public Attr attr(AttrList al, Defn d) { return new Attr(al, d); }
        public AttrList emptyAttrList() { return new AttrList(); }
        public AttrList attrListBuild (Token t, AttrList l) { l.add(t); return l; }
        public Property property (Token n, Type t, Optional<Expr> e) 
        {
            if (e.isPresent()) 
                return property(n,Optional.of(t), e.get()); 
            else
                return property(n,Optional.of(t),Collections.emptyList()); 
        }
        public Property property (Token n, Expr e)
        {
            return property(n, Optional.empty(), e);
        }
        public Property property (Token n, Optional<Type> t, Expr e) 
        { 
            return property(n,t,Collections.singletonList(new PropertyAccessor(AccessorType.INIT, e))); 
        }
        public Property property (Token n, Optional<Type> t, List<PropertyAccessor> a) { return new Property(n.text(),t,a); }
        public PropertyAccessor propertyAccessor (Token t, Expr e) {
            return new PropertyAccessor(accessorType(t), e);
        }
        public PropertyAccessor propertyAccessor (Token t, Stmt s) {
            return new PropertyAccessor(accessorType(t), s);
        }
        public AccessorType accessorType (Token t) {
            if (t.text().equals("get")) return AccessorType.GET;
            if (t.text().equals("set")) return AccessorType.SET;
            if (t.text().equals("init")) return AccessorType.INIT;
            if (t.text().equals("beforeSet")) return AccessorType.BEFORE_SET;
            if (t.text().equals("afterSet")) return AccessorType.AFTER_SET;
            if (t.text().equals("beforeGet")) return AccessorType.BEFORE_GET;
            if (t.text().equals("afterInit")) return AccessorType.AFTER_INIT;
            throw new IllegalArgumentException(t.text());
        }
        public ForAll forall (Pair<List<String>, List<Type.Constraint>> args, Defn d)
        {
            return new ForAll(args.v1,args.v2,d);
        }
        public InterfaceDef iface (Token n, Optional<Pair<List<String>, List<Type.Constraint>>> ao, Optional<List<Type>> si, Optional<List<String>> pl, List<Defn> me)
        {
            Pair<List<String>, List<Type.Constraint>> args = ao.orElse(new Pair<>(Collections.emptyList(),Collections.emptyList()));
            return new InterfaceDef (n.text(), args.v1, args.v2, si.orElse(Collections.emptyList()), pl, me);
        }
        public ImplementationDef impl (Token in, Optional<List<Type>> a, List<Type> i, List<Defn> m)
        {
            return new ImplementationDef(in.text(), a.orElse(Collections.emptyList()), Optional.of(i), m);
        } 
    }
}
