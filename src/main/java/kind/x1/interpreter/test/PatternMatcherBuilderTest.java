package kind.x1.interpreter.test;

import kind.x1.*;
import kind.x1.ast.*;
import kind.x1.misc.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.patterns.*;
import kind.x1.interpreter.types.TypeReference;

import java.util.List;
import java.util.Collections;

public class PatternMatcherBuilderTest extends Assertions implements Runnable
{
    public void run ()
    {
        namedWithoutType();        
        namedWithType();
    }
    
    public void namedWithoutType ()
    {
        PatternMatcherBuilder b = new PatternMatcherBuilder();
        b.beginNamed("name");
        b.endNamed();
        PatternMatcher pm = b.build();
        assertEqual ("namedWithoutType: type", pm.getClass(), NamedPatternMatcher.class);
        assertEqual ("namedWithoutType: variable name", ((NamedPatternMatcher)pm).getVariableName(), "name");
        assertEqual ("namedWithoutType: filter", ((NamedPatternMatcher)pm).getFilter(), PatternMatcher.ACCEPT);
    }
    public void namedWithType ()
    {
        PatternMatcherBuilder b = new PatternMatcherBuilder();
        TypeVisitor tv = b.beginNamed("name");
        tv.namedType (SID.from("type"));
        b.endNamed();
        PatternMatcher pm = b.build();
        assertEqual ("namedWithType: type", pm.getClass(), NamedPatternMatcher.class);
        assertEqual ("namedWithType: variable name", ((NamedPatternMatcher)pm).getVariableName(), "name");
        assertEqual ("namedWithType: filter type", ((NamedPatternMatcher)pm).getFilter().getClass(), TypeCheckingPatternMatcher.class);
        assertEqual ("namedWithType: type reference", 
            ((TypeCheckingPatternMatcher)(((NamedPatternMatcher)pm).getFilter())).getType(), 
            new TypeReference(SID.from("type")));
        
    }
}
