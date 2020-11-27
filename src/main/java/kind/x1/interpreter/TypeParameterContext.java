package kind.x1.interpreter;

import kind.x1.interpreter.*;
import kind.x1.DiagnosticProducer;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.values.KVal;
import kind.x1.interpreter.values.KCallable;
import java.util.*;

public class TypeParameterContext 
{
    public class Parameter implements Type, MemberResolver
    {
        private Optional<Type> unifiedWith = Optional.empty();
        private String name;
        private boolean implicit;
        
        public Parameter(String name, boolean implicit) { this.name = name; this.implicit = implicit; }
        public String getName () { return name; } 
        public boolean isFullyResolved () { return ! unifiedWith.isPresent(); }
        public Optional<Type> resolve (Resolver r, DiagnosticProducer diag)
        {
            return unifiedWith.isPresent() ? unifiedWith.get().resolve(r, diag) : Optional.of(this);
        }
        public boolean isSubTypeOf(Type t) { return false; /*FIXME*/ }
        
        public Optional<Type> getMemberType (String id)
        {
            // FIXME scan constraints for one that provides id
            return Optional.empty();
        }
	public Optional<KVal> getMemberValue (KVal object, String id)
	{
	    // FIXME find constraint proof that provides id and invoke it to fetch value
	    return Optional.empty();
        }
        public Optional<Type> getMemberOperatorType (String name)
        {
            // FIXME scan constraints for one that provides operator
            return Optional.empty();
        }
	public Optional<KCallable> getMemberOperator (KVal object, String name)
	{
	    // FIXME find constraint proof that provides operator and invoke it to fetch and bind value
	    return Optional.empty();
        }
	
        public boolean isImplicit () { return implicit; }
        public void addInferredConstraint (Constraint c)
        {
            if (!implicit) 
                throw new IllegalArgumentException("Attempted to add constraint " + c.getDescription() + " to explicit type parameter " + name);
            addConstraint(c);
        }
        public void unifyWith (Type t) 
        { 
            if (t == this) throw new IllegalArgumentException ("Cannot unify a parameter with itself");
            unifiedWith = Optional.of(t); 
        }
        public void unifyWith (Optional<Type> t) 
        { 
            if (t.isPresent() && t.get() == this) throw new IllegalArgumentException ("Cannot unify a parameter with itself");
            unifiedWith = t; 
        }
        
        public String toString()
        {
            return "TypeParameter<"+name+" => " + unifiedWith +">";
        }
    } 
    public class Constructor implements Type
    {
        private Type type;
        
        public Constructor(Type type) { this.type = type; }
        public String getName () 
        { 
            StringBuilder sb = new StringBuilder("forall ");
            String sep = "";
            for (Parameter p:parameters)
            {
                sb.append(sep).append(p.getName());
                sep = ", ";
            }
            sep = " : ";
            for (Constraint c : constraints)
            {
                sb.append(sep).append(c.getDescription());
                sep = ", ";
            }
            sb.append(" . ").append(type.getName());
            return sb.toString(); 
        } 
        public boolean isFullyResolved () { return type.isFullyResolved(); }
        public Optional<Type> resolve (Resolver r, DiagnosticProducer diag)
        {
            return Optional.of(this);
        }
        public boolean isSubTypeOf(Type t) { return false; /*FIXME*/ }
        
    } 
    
    private List<Parameter> parameters = new ArrayList<>();
    private List<Constraint> constraints = new ArrayList<>();
    
    private int counter = 0;
    
    public Parameter addImplicit ()
    {
        Parameter p = new Parameter("_"+(++counter), true);
        parameters.add(p);
        return p;
    }
    public Parameter addExplicit (String name)
    {
        Parameter p = new Parameter(name, false);
        parameters.add(p);
        return p;
    }
    public void addConstraint(Constraint c) { constraints.add(c); }
    public Constructor getConstructor (Type type)
    {
        return new Constructor(type);
    }
    
    public List<Parameter> getParameters () { return parameters; }
    public List<Constraint> getConstraints () { return constraints; }

    public String toString ()
    {
	if (parameters.isEmpty()) return "/* no type parameters */";
	StringBuilder b = new StringBuilder ("forall ");

	String s = "";
	for (Parameter p : parameters)
	{
	    b.append (s).append(p.getName());
	    s = ", ";
	}
	s = " : ";
	for (Constraint c : constraints)
	{
	    b.append(s).append(c.getDescription());
	    s = ", ";
	}
        
	return b.toString();
    }
}   
