package kind.x1;

import java.util.*;
/** Because Android's class lib doesn't have java.util.Optional. */
public class Optional<T> 
{
    private final boolean present;
    private final T value;
    
    private Optional (boolean p, T v) { present = p; value = v; } 
    
    public boolean isPresent () { return present; }
    public T get()
    {
        if (!present) throw new NoSuchElementException("Empty optional");
        return value;
    }
    public T orElse(T def)
    {
        if (!present) return def;
        return value;
    }
    public <U> Optional<U> map (Mapper<? super T,? extends U> mapper)
    {
        if (!present) return empty();
        return of(mapper.map(value));
    }
    public <U> Optional<U> flatMap (Mapper<? super T,Optional<U>> mapper)
    {
        if (!present) return empty();
        return mapper.map(value);
    }
    
    public boolean equals (Object o)
    {
        if (!(o instanceof Optional)) return false;
        Optional oo = (Optional)o;
        if (present)
            return oo.present && value.equals(oo.value);
        else
            return !oo.present;
    }
    public int hashCode()
    {
        return present ? 1^(value.hashCode()*3):0;
    }
    public String toString() 
    {
        return present ? "Optional.of("+value+")" : "Optional.empty";
    }
    public static <T> Optional<T> empty() { return new Optional<>(false, null); }
    public static <T> Optional<T> of(T v) { return new Optional<>(true, v); }
    public static <T> Optional<T> ofNullable(T v) { return new Optional<>(v != null, v); }
    
}
