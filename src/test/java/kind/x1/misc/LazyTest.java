package kind.x1.misc;

import java.util.concurrent.atomic.*;

import org.junit.*;
import static org.junit.Assert.*;

public class LazyTest
{
    @Test
    public void getsCorrectValue()
    {
	Lazy<String> sut = new Lazy<>(() -> "test");
        assertEquals ("value", sut.get(), "test");
    }
    @Test
    public void onlyCallsSupplierOnce()
    {
	AtomicInteger count = new AtomicInteger(0);
	Lazy<String> sut = new Lazy<>(() -> {
		count.incrementAndGet();
		return "test";
	});
	sut.get();
	sut.get();
        assertEquals ("call count", count.get(), 1);
    }
}

