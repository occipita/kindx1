package kind.x1.interpreter.patterns;

import kind.x1.ast.PatternVisitor;
import kind.x1.ast.TypeVisitor;
import kind.x1.Factory;
import kind.x1.interpreter.types.*;
import java.util.Optional;

public class PatternMatcherBuilder extends PatternVisitor
{
    private PatternMatcher building = PatternMatcher.ACCEPT;
    
    private Factory<TypeBuilder> typeBuilderFactory = TypeBuilder.FACTORY;
    
    private String name;
    private TypeBuilder typeBuilder;
    
    public PatternMatcher build() { return building; }
    
    public TypeVisitor beginNamed (String name) 
    {
        this.name = name; 
        return typeBuilder = typeBuilderFactory.create(); 
    }
    public void endNamed () 
    {
        Type type = typeBuilder.build();
        
        building = new NamedPatternMatcher (name, 
            type == null ? PatternMatcher.ACCEPT
                         : new TypeCheckingPatternMatcher(type)); 
    }   

    
    public final static Factory<PatternMatcherBuilder> FACTORY = new Factory<PatternMatcherBuilder> () {
        public PatternMatcherBuilder create() { return new PatternMatcherBuilder(); }
    };
}
