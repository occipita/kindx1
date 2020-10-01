package kind.x1.interpreter.patterns;

public class PatternNotMatchedException extends Exception
{
    private final PatternMatcher source;
    
    public PatternNotMatchedException (PatternMatcher source)
    {
	super ("Failed to match argument against pattern");
	this.source = source;
    }

    public PatternMatcher getSource() { return source; }

}
