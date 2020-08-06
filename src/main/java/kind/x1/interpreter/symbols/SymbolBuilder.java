package kind.x1.interpreter.symbols;

import kind.x1.ast.*;
import kind.x1.*;
import kind.x1.misc.SID;
import kind.x1.interpreter.executables.*;
import kind.x1.interpreter.patterns.*;
import kind.x1.interpreter.types.TypeBuilder;
import kind.x1.interpreter.types.ConstraintBuilder;
import kind.x1.interpreter.TypeParameterContext;

import java.util.List;
import java.util.Optional;

public class SymbolBuilder extends DefnVisitor
{
    private Symbol building;
    private Optional<TypeParameterContext> context = Optional.empty();
    
    private Factory<ExecutableBuilder> executableBuilderFactory = ExecutableBuilder.FACTORY;
    private Factory<TypeBuilder> typeBuilderFactory = TypeBuilder.FACTORY;
    private Factory<ConstraintBuilder> constraintBuilderFactory = ConstraintBuilder.FACTORY;
    private Factory<PatternMatcherBuilder> patternMatcherBuilderFactory = PatternMatcherBuilder.FACTORY;
    
    private SymbolBuilder childBuilder;
    private FunctionBuilder functionBuilder;
    
    public Symbol build() { return building; }
    public void setExecutableBuilderFactory (Factory<ExecutableBuilder> f) { executableBuilderFactory = f; }
    public void setTypeBuilderFactory (Factory<TypeBuilder> f) { typeBuilderFactory = f; }
    public void setConstraintBuilderFactory (Factory<ConstraintBuilder> f) { constraintBuilderFactory = f; }
    public void setPatternMatcherBuilderFactory (Factory<PatternMatcherBuilder> f) { patternMatcherBuilderFactory = f; }
    
    public DefnVisitor property (String name, Optional<Type> type) //FIXME should be TypeVisitor.Visitable (or perhaps method to ret a visitor)
    {
        TypeBuilder tb = typeBuilderFactory.create();
        type.get().visit(tb);
        building = new PropertySymbol(name, tb.build()); 
        return this; 
    }
    public void propertyAccessor (Defn.AccessorType type, StmtVisitor.Visitable statement) 
    {
        ExecutableBuilder eb = executableBuilderFactory.create();
        statement.visit (eb);
        Executable e = eb.build();
        ((PropertySymbol)building).addAccessor (type, e); 
    }
    
    public DefnVisitor beginInterface (String name) 
    { 
        building = new InterfaceSymbol(name);
        return this; 
    }
    
    public void interfaceParameter (String name) { ((InterfaceSymbol)building).getParameters().add(name); }
    public void interfaceConstraint (Type.Constraint c) { 
        ConstraintBuilder b = constraintBuilderFactory.create();
        c.visit(b);
        ((InterfaceSymbol)building).getParameterConstraints().add(b.build());
    }    
    public void interfaceSuperinterface (Type t) {
        TypeBuilder b = typeBuilderFactory.create();
        t.visit(b);
        ((InterfaceSymbol)building).getSuperinterfaces().add(b.build());        
    }
    public void interfacePlaceholder (List<String> names) { ((InterfaceSymbol)building).getPlaceholders().addAll(names); }
    public DefnVisitor visitInterfaceMember () 
    { 
        // if a child is already buikt add it:
        if (childBuilder != null) addInterfaceMember(childBuilder.build());
        // now make a builder to create the new member:
        childBuilder = new SymbolBuilder(); 
        // child builder should inherit our options:
        childBuilder.setExecutableBuilderFactory(executableBuilderFactory);
        childBuilder.setConstraintBuilderFactory(constraintBuilderFactory);
        childBuilder.setPatternMatcherBuilderFactory(patternMatcherBuilderFactory);
        childBuilder.setTypeBuilderFactory(typeBuilderFactory);
        return childBuilder;
    }
    public void endInterface () {
        if (childBuilder != null) addInterfaceMember(childBuilder.build());
    }
    
    private void addInterfaceMember (Symbol s)
    {
        ((InterfaceSymbol)building).getEntries().add(s);
    }
    
    public DefnVisitor beginFunction (String name) 
    { 
        functionBuilder = new FunctionBuilder (name, context);
        functionBuilder.setExecutableBuilderFactory(executableBuilderFactory);
        functionBuilder.setConstraintBuilderFactory(constraintBuilderFactory);
        functionBuilder.setPatternMatcherBuilderFactory(patternMatcherBuilderFactory);
        functionBuilder.setTypeBuilderFactory(typeBuilderFactory);
        
        return functionBuilder; 
    }
    public DefnVisitor beginOperatorFunction (String symbol) { return null; }
    public void endFunction () { building = functionBuilder.build(); }

    public void beginForAll () { context = Optional.of(new TypeParameterContext()); }
    public void typeParameter (String name) { context.get().addExplicit(name); }
    public void typeConstraint (Type.Constraint c) { 
        ConstraintBuilder b = constraintBuilderFactory.create();
        c.visit(b);
        context.get().addConstraint(b.build());
    }    
        
    public static final Factory<SymbolBuilder> FACTORY = new Factory<SymbolBuilder>() {
        public SymbolBuilder create() { return new SymbolBuilder(); } 
    };
    
    
}
