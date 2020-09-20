package kind.x1.interpreter.executables;

import kind.x1.interpreter.Resolver;

public interface Executable {
    public static final Executable NULL_EXECUTABLE = new Executable() {
	public String toString () { return "<null executable>"; }
    };

    default void execute (Resolver resolver) { }
}
