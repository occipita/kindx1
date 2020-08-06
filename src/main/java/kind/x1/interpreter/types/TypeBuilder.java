package kind.x1.interpreter.types;

import kind.x1.ast.TypeVisitor;
import kind.x1.ast.ExprVisitor;
import kind.x1.misc.SID;
import kind.x1.*;
import java.util.ArrayList;
import java.util.Optional;

public class TypeBuilder extends TypeVisitor
{
    Type building;
    ArrayList<Type> tlist;
    Optional<Type> opt = Optional.empty();
    TypeBuilder listBuilder, optBuilder;
            
    public Type build()
    {
        // FIXME resolve before returning where possible
        return building;
    }
    
    public void namedType (SID name) 
    {
        building = new TypeReference (name); 
    }
    public void expression (ExprVisitor.Visitable expr) { }
    public void beginConstructorCall () { }
    public TypeVisitor visitConstructor () { return null; }
    public TypeVisitor visitConstructorArg () { return null; }
    public void endConstructorCall () { }

    public void beginFunction () { tlist = new ArrayList<>(); }
    /** Called omce for each function parameter */
    public TypeVisitor visitFunctionParameter () { return newListBuilder(); }
    /** Called once if the function returns a value (otherwise function is assumed to be noreturn) */
    public TypeVisitor visitFunctionReturnType () { return optBuilder =newSubBuilder(); }
    public void endFunction () 
    {
        buildList();
        buildOpt();
        building = new FunctionType (tlist, opt);
    }

    public void voidType () { building = new VoidType(); }

    private TypeBuilder newListBuilder()
    {
        buildList();
        return listBuilder = newSubBuilder();
    }
    private TypeBuilder newSubBuilder() { return FACTORY.create(); }
    private void buildList()
    {
        if (listBuilder != null) tlist.add(listBuilder.build());
    }
    private void buildOpt()
    {
        if (optBuilder != null) opt = Optional.ofNullable(optBuilder.build());
    }
    public static final Factory<TypeBuilder> FACTORY = new Factory<TypeBuilder>() {
        public TypeBuilder create() { return new TypeBuilder(); }
    };
    
}
