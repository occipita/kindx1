package kind.x1;

public class Assertions 
{
    protected void assertEqual (String msg, Object a, Object b)
    {
        if ((a == null && b != null) || (a != null && b == null) || (a != null && !a.equals(b)))
        {
            String aStr = ""+a;
            String bStr = ""+b;
            if (aStr.length()>20)
                fail(msg + "\n    " + aStr + "\n != " + bStr);
            else
                fail(msg + " " + aStr + " != " + bStr);
        }
    }

    protected void assertTrue (String msg, boolean flag)
    {
        if (!flag)
            fail(msg);
    }           

    protected void assertFalse (String msg, boolean flag) { assertTrue (msg, !flag); }
    protected void fail(String msg) { System.err.println(getClass().getName()+"."+msg); }
}
