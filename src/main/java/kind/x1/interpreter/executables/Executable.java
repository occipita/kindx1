package kind.x1.interpreter.executables;

public interface Executable {
    public static final Executable NULL_EXECUTABLE = new Executable() {
	public String toString () { return "<null executable>"; }
    };
}
