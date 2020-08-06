package kind.x1;

import java.util.Collections;
import java.util.List;


public class Mappers {
    
    public static final Mapper<Object,String> MAP_TO_STRING = new Mapper<Object,String>(){
        public String map (Object o) { return o.toString(); }
    };
    public static <T> Mapper<List<T>,List<T>> mapToUnmodifiableList()
    {
        return new Mapper<List<T>,List<T>>() {
            public List<T> map (List<T> o) { return Collections.unmodifiableList(o); }
        };
    }
    public static <T> Mapper<T,Optional<T>> mapToOptional()
    {
        return new Mapper<T,Optional<T>>() {
            public Optional<T> map (T o) { return Optional.of(o); }
        };
    }
    public static <T> Mapper<T,Optional<T>> mapNullableToOptional()
    {
        return new Mapper<T,Optional<T>>() {
            public Optional<T> map (T o) { return Optional.ofNullable(o); }
        };
    }
    

}
