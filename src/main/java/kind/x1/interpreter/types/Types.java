package kind.x1.interpreter.types;

import kind.x1.misc.*;

/** utility methods that manipulate types */
public class Types {
    public static Predicate<Type> subTypePredicate (final Type sup)
    {
        return new Predicate<Type>() {
            public boolean test (Type t) { return t.isSubTypeOf (sup); }
        };
    }
}
