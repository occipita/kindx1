package kind.x1.ast;

import kind.x1.Optional;
import java.util.List;

public class DefnVisitor 
{
    public interface Visitable { void visit (DefnVisitor visitor); }
    
    /** Called to define a property
     *  @return a visitor to receive accessor definitions, or null to skip accessors
     */
    public DefnVisitor property (String name, Optional<Type> type) { return null; }
    public void propertyAccessor (Defn.AccessorType type, StmtVisitor.Visitable statement) { }
    public void endProperty () { }

    public DefnVisitor beginInterface (String name) { return null; }
    public void interfaceParameter (String name) { }
    public void interfaceConstraint (Type.Constraint c) { }    
    public void interfaceSuperinterface (Type t) { }
    public void interfacePlaceholder (List<String> names) { }
    public DefnVisitor visitInterfaceMember () { return null; }
    public void endInterface () { }
    
    public DefnVisitor beginFunction (String name) { return null; }
    public DefnVisitor beginOperatorFunction (String symbol) { return null; }
    public void beginAbstractBody () { }
    public void beginImplementationBody () { }
    public void parameterPattern (PatternVisitor.Visitable pattern) { }
    public void returnType (TypeVisitor.Visitable type) { }
    public void bodyImplementation (StmtVisitor.Visitable stmt) { }
    public void endFunctionBody () { }
    public void endFunction () { }
    
    /** Called for a 'forall' block; will be followed by parameters, then constraints, then the contained object */
    public void beginForAll () { }
    public void typeParameter (String name) { }
    public void typeConstraint (Type.Constraint constraint) { }
}
