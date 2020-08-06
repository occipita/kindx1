package kind.x1.interpreter.types;

/** 
 * Describes the expected type of an expression being type checked.
 * Expected types may be unspecified, specified exactly, required to be
 * a subtype of a given type, or required to be a supertype of a given type.
 */
 
import kind.x1.*;
import kind.x1.interpreter.TypeParameterContext;

public class TypeSpec 
{
    public enum Mode { 
        UNSPECIFIED { boolean matches (Type p, Type t) { return true; } }, 
        EXACT       { boolean matches (Type p, Type t) { return t.equals(p); } }, 
        SUBTYPE     { boolean matches (Type p, Type t) { return t.equals(p); /* FIXME */ } }, 
        SUPERTYPE   { boolean matches (Type p, Type t) { return false; /* FIXME */ } };
        
        abstract boolean matches (Type p, Type t); 
    } 
    
    private Mode mode;
    private Type type; // may be null iff mode == UNSPECIFIED
    
    private TypeSpec (Mode m, Type t) { mode = m; type = t; }
    
    public boolean matches (Type t) { return mode.matches (type, t); }
    
    public Mode getMode() { return mode; }
    public Type getType() { return type; } 
    /** Returns an Optional containing the type which shouod be inferred in order
     * to match this type spec, if possible. Returns empty for #UNSPECIFIED */
    public Optional<Type> getInferredType () {
        if (mode == Mode.UNSPECIFIED) return Optional.empty();
        return Optional.of(type);
    }
    
    public TypeSpec mapType (Mapper<Type,Type> mapper)
    {
        if (mode == Mode.UNSPECIFIED) return this;
        return new TypeSpec (mode, mapper.map(type));
    }
    
    public boolean isMoreSpecificThan (TypeSpec ts)
    {
        if (mode == Mode.UNSPECIFIED) return false;
        if (ts.mode == Mode.UNSPECIFIED) return true;
        // type parameters are less specific than everything else
        if (type instanceof TypeParameterContext.Parameter) return false;
        if (ts.type instanceof TypeParameterContext.Parameter) return true;
        // FIXME other cases (eg subtypes)
        return false;
    }
    public boolean isRefType() { return mode != Mode.UNSPECIFIED && type instanceof Ref; }
    
    public String toString () { return "TypeSpec<"+mode+","+type+">"; }
    
    public static TypeSpec UNSPECIFIED = new TypeSpec(Mode.UNSPECIFIED, null);
    public static TypeSpec exactly(Type t) { return new TypeSpec(Mode.EXACT, t); }
    public static TypeSpec subtypeOf(Type t) { return new TypeSpec(Mode.SUBTYPE, t); }
    public static TypeSpec supertypeOf(Type t) { return new TypeSpec(Mode.SUPERTYPE, t); }
}
