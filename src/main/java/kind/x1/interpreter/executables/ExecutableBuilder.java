package kind.x1.interpreter.executables;

import kind.x1.ast.*;
import kind.x1.Factory;
import java.util.*;

public class ExecutableBuilder extends StmtVisitor
{
    private Executable building = Executable.NULL_EXECUTABLE; // default if no visitor methods called
    private Factory<EvaluatableBuilder> evaluatableBuilderFactory = EvaluatableBuilder.FACTORY;
    private List<Executable> children;
    private ExecutableBuilder childBuilder;
    
    public void setEvaluatableBuilderFactory(Factory<EvaluatableBuilder> f) { evaluatableBuilderFactory = f; }
    public Executable build() { return building; }
    
    public void ret (ExprVisitor.Visitable expr) 
    {
        EvaluatableBuilder b = evaluatableBuilderFactory.create();
        expr.visit(b);
        building = new ReturnValueExecutable (b.build());  
    }
    public void retVoid () { building = new ReturnExecutable(); }
    
    public void expression (ExprVisitor.Visitable expr) 
    {
        EvaluatableBuilder b = evaluatableBuilderFactory.create();
        expr.visit(b);
        building = new EvaluatableExecutable (b.build());   
    } 

    public void beginBlock () { children = new ArrayList<>(); }
    public StmtVisitor visitBlockChild()
    {
        addBuildingChild();
        childBuilder = new ExecutableBuilder();
        childBuilder.setEvaluatableBuilderFactory(evaluatableBuilderFactory);
        return childBuilder;
    }    
    private void addBuildingChild()
    {
        if (childBuilder != null) children.add (childBuilder.build());
    }
    public void endBlock()
    {
        addBuildingChild();
        building = new SequenceExecutable(children);
    }
    public static final Factory<ExecutableBuilder> FACTORY = new Factory<ExecutableBuilder>() {
        public ExecutableBuilder create() { return new ExecutableBuilder(); }
    };
}
