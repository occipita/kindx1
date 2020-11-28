package kind.x1;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.Optional;


public class Mappers {
    
    public static final Function<Object,String> MAP_TO_STRING = new Function<Object,String>(){
        public String apply (Object o) { return o.toString(); }
    };
    public static <T> Function<List<T>,List<T>> mapToUnmodifiableList()
    {
        return new Function<List<T>,List<T>>() {
            public List<T> apply (List<T> o) { return Collections.unmodifiableList(o); }
        };
    }
    public static <T> Function<T,Optional<T>> mapToOptional()
    {
        return new Function<T,Optional<T>>() {
            public Optional<T> apply (T o) { return Optional.of(o); }
        };
    }
    public static <T> Function<T,Optional<T>> mapNullableToOptional()
    {
        return new Function<T,Optional<T>>() {
            public Optional<T> apply (T o) { return Optional.ofNullable(o); }
        };
    }
    public static <S,T extends S> Function<S, Optional<T>> safeCaster(Class<S> source, Class<T> target)
    {
	return obj -> {
	    try {
		return Optional.ofNullable(target.cast(obj));
	    } catch (ClassCastException ex) {
		return Optional.empty();
	    }
	};
    }

}
