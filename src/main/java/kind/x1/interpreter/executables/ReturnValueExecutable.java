package kind.x1.interpreter.executables;

public class ReturnValueExecutable implements Executable
{
    private Evaluatable evaluatable;
    
    public ReturnValueExecutable(Evaluatable e) { evaluatable = e; }
    public Evaluatable getEvaluatable() { return evaluatable; }
    public String toString () { return "<ret " + evaluatable + ">"; }
}
