package kind.x1;

public class Pair<T1,T2> 
{
    public final T1 v1;
    public final T2 v2;
    public Pair (T1 a, T2 b) { v1 = a; v2 = b; }
    public boolean equals (Object o)
    {
        if (!(o instanceof Pair)) return false;
        Pair<?,?> p = (Pair<?,?>)o;
        return (v1 == null ? p.v1 == null : v1.equals(p.v1)) &&
            (v2 == null ? p.v2 == null : v2.equals(p.v2));
    }
    public int hashCode()
    {
        return (v1 == null ? 0 : v1.hashCode()) * 11 +
            (v2 == null ? 0 : v2.hashCode()) * 17;
    }
    public String toString() { return "<"+v1+","+v2+">"; }
}
