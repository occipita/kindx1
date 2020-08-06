package kind.x1.interpreter.symbols;

import kind.x1.ast.DefnVisitor;
import kind.x1.ast.PatternVisitor;
import kind.x1.ast.StmtVisitor;
import kind.x1.ast.TypeVisitor;
import kind.x1.*;
import kind.x1.misc.SID;
import java.util.List;
import kind.x1.interpreter.types.TypeBuilder;
import kind.x1.interpreter.types.ConstraintBuilder;
import kind.x1.interpreter.executables.ExecutableBuilder;
import kind.x1.interpreter.patterns.PatternMatcherBuilder;
import kind.x1.interpreter.TypeParameterContext;

import java.util.ArrayList;
import java.util.Optional;

public class FunctionBuilder extends DefnVisitor
{
    private FunctionSymbol fn;

    private Factory<ExecutableBuilder> executableBuilderFactory = ExecutableBuilder.FACTORY;
    private Factory<TypeBuilder> typeBuilderFactory = TypeBuilder.FACTORY;
    private Factory<ConstraintBuilder> constraintBuilderFactory = ConstraintBuilder.FACTORY;
    private Factory<PatternMatcherBuilder> patternMatcherBuilderFactory = PatternMatcherBuilder.FACTORY;
    
    public FunctionBuilder (String n, Optional<TypeParameterContext> tpc) { fn = new FunctionSymbol(n, tpc); } 
    
    public void setExecutableBuilderFactory (Factory<ExecutableBuilder> f) { executableBuilderFactory = f; }
    public void setTypeBuilderFactory (Factory<TypeBuilder> f) { typeBuilderFactory = f; }
    public void setConstraintBuilderFactory (Factory<ConstraintBuilder> f) { constraintBuilderFactory = f; }
    public void setPatternMatcherBuilderFactory (Factory<PatternMatcherBuilder> f) { patternMatcherBuilderFactory = f; }

    public void beginAbstractBody () { fn.setAbstract(true); }
    public void beginImplementationBody () { }
    public void parameterPattern (PatternVisitor.Visitable pattern) { 
        PatternMatcherBuilder pmb = patternMatcherBuilderFactory.create();
        pattern.visit(pmb);
        fn.getParameters().add(pmb.build());
    }
    public void returnType (TypeVisitor.Visitable type) { 
        TypeBuilder tb = typeBuilderFactory.create();
        type.visit(tb);
        fn.setReturnType(tb.build());
    }
    public void bodyImplementation (StmtVisitor.Visitable stmt) {
        ExecutableBuilder eb = executableBuilderFactory.create();
        stmt.visit(eb);
        fn.setExecutable(Optional.of(eb.build()));
    }
    public void endFunctionBody () { }
    
    public FunctionSymbol build() { return fn; }
}
