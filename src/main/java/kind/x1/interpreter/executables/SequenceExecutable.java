package kind.x1.interpreter.executables;

import java.util.List;

public class SequenceExecutable implements Executable
{
    private List<Executable> executables;
    
    public SequenceExecutable(List<Executable> e) { executables = e; }
    public List<Executable> getExecutables() { return executables; }
    public String toString() { return "<sequence " + executables + ">"; }
}
