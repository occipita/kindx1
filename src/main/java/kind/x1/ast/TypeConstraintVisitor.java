package kind.x1.ast;

import kind.x1.misc.SID;

public interface TypeConstraintVisitor 
{
    TypeConstraintVisitor relation (SID r);
    TypeConstraintVisitor parameter (TypeVisitor.Visitable t);
}
