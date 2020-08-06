package kind.x1;

import kind.x1.interpreter.test.*;

public class RunInterpreterTests
{
    public static void main(String [] args)
    {
        boolean slowTests = false;
        long st = System.currentTimeMillis();
         
        new ModuleBuilderTest().run();        
        new SymbolBuilderTest().run();
        new TypeBuilderTest().run();
        new ConstraintBuilderTest().run();
        new PatternMatcherBuilderTest().run();
        new ExecutableBuilderTest().run();
        new EvaluatableBuilderTest().run();
        new ResolutionTest().run();
        new TypeSpecTest().run();
        new InferenceTest().run();
        new InferenceTest2().run();
        
        System.out.println((slowTests ? "All" : "Only fast") + " tests completed in " + (System.currentTimeMillis()-st) + "ms");
    }
}