package kind.x1;

/** A Factory that always produces the same value */
public class StaticFactory<T> implements Factory<T> 
{
    private T value;
    
    public StaticFactory (T v) { value = v; }
    public T create () { return value; }
}
