package kind.x1;

import kind.x1.test.*;

public class RunUnitTests
{
    public static void main(String [] args)
    {
        boolean slowTests = false;
        long st = System.currentTimeMillis();
         
        new NDFATest().run();
        new LexerTest().run();
        new TokenStreamMutatorTest().run();
        new GSSTest().run();
        new ATNTest().run();
        new ALLStarTest().run();
        if (slowTests) new ExprTest().run();
        new StmtTest().run();
        new TypeTest().run();
        new PatternTest().run();
        new DefnTest().run();
        new ModuleTest().run();
        
        
        System.out.println((slowTests ? "All" : "Only fast") + " tests completed in " + (System.currentTimeMillis()-st) + "ms");
    }
}