package kind.x1.misc;

import java.util.function.Supplier;

public class Lazy<T>
{
    private boolean initialized;
    private T value;
    private Supplier<T> supplier;

    public Lazy(Supplier<T> supplier) { this.supplier = supplier; }
    public synchronized T get () {
	if (!initialized) {
	    value = supplier.get();
	    initialized = true;
	    supplier = null;
	}
	return value;
    }
}
    
