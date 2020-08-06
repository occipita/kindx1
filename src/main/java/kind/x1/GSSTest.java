package kind.x1;

import java.util.Iterator;

public class GSSTest extends Assertions implements Runnable
{
    public void run()
    {
        testSingleStack();
        testMergedStack();
    }
    
    public void testSingleStack ()
    {
        GSS<Integer> s = GSS.empty();
        assertTrue("testSingleStack: empty stack should be empty", s.isEmpty());
        s = s.push (42);
        assertFalse("testSingleStack: after push stack should not be empty", s.isEmpty());
        assertStackTop("testSingleStack: stack top after first push", s, 42);
        s = s.push(13);
        assertStackTop("testSingleStack: stack top after second push", s, 13);
        s = s.pop();
        assertStackTop("testSingleStack: stack top after first pop", s, 42);
        s = s.pop();
        assertTrue("testSingleStack: stack should be empty after second pop", s.isEmpty());   
    }
    
    public void testMergedStack ()
    {
        GSS<Integer> s1 = GSS.<Integer>empty().push(42).push(13);
        GSS<Integer> s2 = GSS.<Integer>empty().push(42).push(27);
        GSS<Integer> s = GSS.merge(s1, s2);
        assertStackTop ("testMergedStack: stack top after merge", s, 13, 27);
        s = s.pop();
        assertStackTop ("testMergedStack: stack top after POP", s, 42);
        s = s.pop();
        assertTrue("testMergedStack: stack should be empty after second pop", s.isEmpty());     
    }
    
    private <T> void assertStackTop (String msg, GSS<T> stack, T... values)
    { 
        Iterator<GSS.Entry<T>> it = stack.iterator();
        for (int i = 0; i < values.length; i++)
        {
            if (!it.hasNext())
            {
                fail (msg + " - expected " + values.length + " entries but got " + i);
                return;
            }
            T val = it.next ().head;
            assertEqual (msg + " value " + (i+1), val, values[i]);
        }
        assertFalse (msg + " unwanted extra values", it.hasNext());
    }
}
