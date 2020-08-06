package kind.x1.interpreter.executables;

public class EvaluatableExecutable implements Executable
{
    private Evaluatable evaluatable;
    
    public EvaluatableExecutable(Evaluatable e) { evaluatable = e; }
    public Evaluatable getEvaluatable() { return evaluatable; }
}
