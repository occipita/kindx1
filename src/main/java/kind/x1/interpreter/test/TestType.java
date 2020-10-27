package kind.x1.interpreter.test;

import kind.x1.interpreter.*;
import kind.x1.interpreter.types.*;
import kind.x1.interpreter.symbols.*;
import kind.x1.interpreter.values.KVal;
import kind.x1.*;
import kind.x1.misc.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;
import java.util.Optional;

public class TestType implements Type, MemberResolver
{
    SID name;
    HashMap<String, Type> members = new HashMap<>();
    HashMap<String, Function<KVal,KVal>> getters = new HashMap<>();
    HashSet<Type> superTypes = new HashSet<>();
    
    public TestType (SID n) { name = n; }
    public TestType (String n) { name = SID.from(n); }
    public TestType addMember (String n, Type t) { members.put(n, t); return this; }
    public TestType addMember (String n, Type t, Function<KVal,KVal> getter) { getters.put(n, getter); return addMember(n, t); }
    public TestType addSuperType (Type t) { superTypes.add(t); return this; }
    
    public String getName() { return name.toString(); }
    public boolean isFullyResolved() { return true; }
    public Optional<Type> resolve (Resolver r, DiagnosticProducer diag) { return Optional.of(this); }
    public boolean isSubTypeOf(Type t) { 
        return t == this || superTypes.contains(t) || Predicates.firstMatch(Types.subTypePredicate(t), superTypes).isPresent();
    }
    public Optional<Type> getMemberType (String name) { return Optional.ofNullable(members.get(name)); }
    public Optional<KVal> getMemberValue (KVal object, String name) { return Optional.ofNullable(getters.get(name)).map(f -> f.apply(object)); }         
}
