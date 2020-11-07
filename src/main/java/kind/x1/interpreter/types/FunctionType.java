package kind.x1.interpreter.types;

import kind.x1.interpreter.*;
import kind.x1.DiagnosticProducer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class FunctionType implements Type
{
    private List<Element> elements;
    public FunctionType (List<Type> p, Optional<Type> r)
    {    
        elements = Collections.singletonList(new Element(p, r, Optional.empty()));
    }
    public FunctionType (List<Type> p, Optional<Type> r, Type thisType)
    {    
        elements = Collections.singletonList(new Element(p, r, Optional.of(thisType)));
    }
    private FunctionType (List<Element> es)
    {    
        elements = es;
    }
    public FunctionType (Iterable<FunctionType> fts)
    {
        elements = new ArrayList<>();
        for (FunctionType ft : fts) elements.addAll(ft.getElements());
    }
    public List<Element> getElements() { return elements; }
    public String getName()
    {
        if (elements.size() == 1)
            return elements.get(0).getName();
        StringBuilder b = new StringBuilder("{");
        String sep = "";
        for (Element e : elements)
        {
            b.append(sep).append(e.getName());
            sep = ", ";
        } 
        return b.append('}').toString();
    }
    public boolean isFullyResolved()
    {
        for (Element e : elements) if (!e.isFullyResolved()) return false;
        return true;
    }
    public Optional<Type> resolve (Resolver r, DiagnosticProducer diag)
    {
        if (isFullyResolved())
            return Optional.of(this);
        List<Element> es = new ArrayList<>();
        for (Element e :elements)
        {
            Optional<Element> en = e.resolve(r, diag);
            if (!en.isPresent()) return Optional.empty();
            es.add (en.get());
        }
        return Optional.of(new FunctionType(es));
    }
    
    public boolean isSubTypeOf(Type t) { return false; /*FIXME*/ }
    
    public Optional<Type> getApplicationResult (List<Type> argTypes)
    {
        for (Element e : elements)
        {
            // FIXME scan for more specific matches before returning?
            if (e.parametersMatch(argTypes)) return e.getReturnType();
        }
        return Optional.empty();
    }

    public String toString() { return getName(); }
    
    public boolean equals (Object obj)
    {
        if (obj.getClass() != getClass()) return false;
        if (!((FunctionType)obj).elements.equals(elements)) return false;
        return true;
    }
    public int hashCode() { return elements.hashCode(); } 

    public static class Element
    {
	private List<Type> parameters;
	private Optional<Type> returnType;
	private Optional<Type> thisType;
    
	public Element (List<Type> p, Optional<Type> r, Optional<Type> tt) { parameters = p; returnType = r; thisType = tt; }
    
	public String getName()
	{
	    StringBuilder b = new StringBuilder("");

	    thisType.ifPresent(t -> b.append(t.getName()).append("::"));
	    
	    String sep = "";
	    b.append("(");
	    for (Type t : parameters)
	    {
		b.append(sep).append(t.getName());
		sep = ", ";
	    }
	    b.append (") -> ");
	    if (returnType.isPresent()) 
		b.append(returnType.get().getName());
	    else
		b.append("noreturn");    
	    return b.toString();
	}
	public boolean isFullyResolved()
	{
	    for (Type t : parameters) if (!t.isFullyResolved()) return false;
	    if (returnType.isPresent() && ! returnType.get().isFullyResolved()) return false;
	    if (thisType.isPresent() && ! thisType.get().isFullyResolved()) return false;
	    return true;
	}
	public Optional<Element> resolve (Resolver r, DiagnosticProducer diag)
	{
	    if (isFullyResolved())
		return Optional.of(this);
	    List<Type> p = new ArrayList<>(parameters.size());
	    for (Type t : parameters)
		if (t.isFullyResolved())
		    p.add(t);
		else
		{
		    Optional<Type> rt = t.resolve(r, diag);
		    if (!rt.isPresent()) return Optional.empty();
		    p.add(rt.get());
		}
	    Optional<Type> rrt = returnType;
	    if (returnType.isPresent() && ! returnType.get().isFullyResolved()) 
	    {
		rrt = returnType.get().resolve(r, diag);
		if (!rrt.isPresent()) return Optional.empty();
	    }
	    Optional<Type> rtt = thisType;
	    if (thisType.isPresent() && ! thisType.get().isFullyResolved()) 
	    {
		rtt = thisType.get().resolve(r, diag);
		if (!rtt.isPresent()) return Optional.empty();
	    }
	    return Optional.of(new Element(p,rrt,rtt));           
	}
    
	public List<Type> getParameters() { return parameters; }
	public Optional<Type> getReturnType () { return returnType; }
	public Optional<Type> getThisType () { return thisType; }
	public boolean parametersMatch (List<Type> args)
	{
	    if (args.size() != parameters.size()) return false;
	    for (int i = 0; i < args.size(); i++)
		if (!args.get(i).isSubTypeOf(parameters.get(i))) return false;
	    return true;
	}
    
	public boolean equals(Object obj)
	{
	    if (obj.getClass() != getClass()) return false;
	    return returnType.equals(((Element)obj).returnType) &&
		thisType.equals(((Element)obj).thisType) &&
		parameters.equals(((Element)obj).parameters);
	}
	public int hashCode () { return 3*returnType.hashCode()+17*parameters.hashCode()+27*thisType.hashCode(); }
    }

}
