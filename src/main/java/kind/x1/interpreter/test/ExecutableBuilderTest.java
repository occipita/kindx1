package kind.x1.interpreter.test;

import kind.x1.*;
import kind.x1.ast.*;
import kind.x1.misc.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.executables.*;

import java.util.List;
import java.util.Collections;

public class ExecutableBuilderTest extends Assertions implements Runnable
{
    public void run ()
    {
        ret(); 
        voidRet();
        expression();
        block ();  
    }
    
    public void ret ()
    {
        ExecutableBuilder b = new ExecutableBuilder();
        TestEvaluatable expr = new TestEvaluatable();
        b.setEvaluatableBuilderFactory (TestEvaluatableBuilder.factoryReturning(expr));
        b.ret (new Expr.IntLiteral("42", Collections.emptyList()));
        Executable e = b.build();
        assertEqual ("ret: type", e.getClass(), ReturnValueExecutable.class);
        assertEqual ("ret: evaluatable", ((ReturnValueExecutable)e).getEvaluatable(), expr);
    }
    public void voidRet ()
    {
        ExecutableBuilder b = new ExecutableBuilder();
        b.retVoid ();
        Executable e = b.build();
        assertEqual ("voidRet: type", e.getClass(), ReturnExecutable.class);
        // FIXME check expression
    }
    public void expression ()
    {
        ExecutableBuilder b = new ExecutableBuilder();
        TestEvaluatable expr = new TestEvaluatable();
        b.setEvaluatableBuilderFactory (TestEvaluatableBuilder.factoryReturning(expr));
        b.expression (new Expr.IntLiteral("42", Collections.emptyList()));
        Executable e = b.build();
        assertEqual ("expression: type", e.getClass(), EvaluatableExecutable.class);
        assertEqual ("expression: evaluatable", ((EvaluatableExecutable)e).getEvaluatable(), expr);
    }
    public void block ()
    {
        ExecutableBuilder b = new ExecutableBuilder();
        b.beginBlock();
        StmtVisitor sub = b.visitBlockChild();
        sub.expression (new Expr.IntLiteral("42", Collections.emptyList()));
        sub = b.visitBlockChild();
        sub.retVoid();
        b.endBlock();
        Executable e = b.build();
        assertEqual ("block: type", e.getClass(), SequenceExecutable.class);
        assertEqual ("block: executables size", ((SequenceExecutable)e).getExecutables().size(), 2);
        assertEqual ("block: executables 0 type", ((SequenceExecutable)e).getExecutables().get(0).getClass(), EvaluatableExecutable.class);
        assertEqual ("block: executables 1 type", ((SequenceExecutable)e).getExecutables().get(1).getClass(), ReturnExecutable.class);
    }
}
