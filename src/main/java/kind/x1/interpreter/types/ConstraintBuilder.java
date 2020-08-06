package kind.x1.interpreter.types;

import kind.x1.ast.TypeVisitor;
import kind.x1.ast.TypeConstraintVisitor;
import kind.x1.*;
import kind.x1.misc.SID;
import java.util.List;
import java.util.ArrayList;

public class ConstraintBuilder implements TypeConstraintVisitor
{
    private Factory<TypeBuilder> typeBuilderFactory = TypeBuilder.FACTORY;
    private SID relation;
    private List<Type> parameters = new ArrayList<>();
    
    public void setTypeBuilderFactory(Factory<TypeBuilder> f) { typeBuilderFactory = f; }
    public ConstraintBuilder relation (SID r) { relation = r; return this; }
    public ConstraintBuilder parameter (TypeVisitor.Visitable type) {
        TypeBuilder tb = typeBuilderFactory.create();
        type.visit(tb);
        parameters.add(tb.build());
        return this;
    }
    public Constraint build() { return new Constraint (relation, parameters); }
     
    public static final Factory<ConstraintBuilder> FACTORY = new Factory<ConstraintBuilder>() {
        public ConstraintBuilder create() { return new ConstraintBuilder(); } 
    };

}
