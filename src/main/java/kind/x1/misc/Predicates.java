package kind.x1.misc;

import kind.x1.Optional;

public class Predicates 
{
    public static <T> Optional<T> firstMatch (Predicate<? super T> predicate, Iterable<? extends T> items)
    {
        for (T t : items) if (predicate.test(t)) return Optional.of(t);
        return Optional.empty();
    }
}
