package kind.x1.interpreter.test;

import kind.x1.*;
import kind.x1.ast.*;
import kind.x1.misc.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.symbols.*;
import kind.x1.interpreter.patterns.*;
import kind.x1.interpreter.types.TypeReference;

import java.util.List;
import java.util.Collections;

public class SymbolBuilderTest extends Assertions implements Runnable
{
    public final static Type INT = new Type.NamedType(SID.from("int"));
    public final static Type T1 = new Type.NamedType(SID.from("T1"));
    
    public void run()
    {
        simpleProperty();
        propertyWithAccessors();
        emptyInterface();
        complexInterface();
        abstractFn();
        interfaceAbstractFn();
        function();
        explicitFunctionType();
        functionTypeWithImpliedPolymorphism();
        functionTypeWithExplicitPolymorphism();
        functionWithConstraint();
    }
    
    public void simpleProperty()
    {
        SymbolBuilder b = new SymbolBuilder ();
        b.property ("test", Optional.of(INT));
        b.endProperty();
        PropertySymbol s = (PropertySymbol)b.build();
        assertEqual("simpleProperty: name", s.getName(), "test");
        assertEqual("simpleProperty: class of type", s.getType().get().getClass(), TypeReference.class);
    }
    
    public void propertyWithAccessors()
    {
        SymbolBuilder b = new SymbolBuilder ();
        DefnVisitor v = b.property ("test", Optional.of(INT));
        v.propertyAccessor(Defn.AccessorType.GET, new Stmt.Ret(new Expr.IntLiteral("42",Collections.emptyList())));
        b.endProperty();
        PropertySymbol s = (PropertySymbol)b.build();
        assertTrue ("propertyWithAccessors: has a getter", s.getAccessor(Defn.AccessorType.GET).isPresent());
    }
    
    public void emptyInterface()
    {
        SymbolBuilder b = new SymbolBuilder ();
        b.beginInterface ("test");
        b.endInterface();
        InterfaceSymbol s = (InterfaceSymbol)b.build();
        assertEqual("emptyInterface: name", s.getName(), "test");
        assertEqual("emptyInterface: entry count", s.getEntries().size(), 0);
    }
    
    public void complexInterface()
    {
        SymbolBuilder b = new SymbolBuilder ();
        DefnVisitor iv = b.beginInterface ("test");
        iv.interfaceParameter("T1");
        iv.interfaceParameter("T2");
        iv.interfaceConstraint(new Type.Constraint(
            SID.from("constraint"), 
            Collections.singletonList(new Type.NamedType(SID.from("T1")))
            ));
        iv.interfaceSuperinterface(new Type.NamedType(SID.from("super1")));
        iv.interfaceSuperinterface(new Type.NamedType(SID.from("super2")));
        iv.interfacePlaceholder(Collections.singletonList("P"));
        b.endInterface();
        InterfaceSymbol s = (InterfaceSymbol)b.build();
        assertEqual("complexInterface: parameters", s.getParameters().toString(), "[T1, T2]");
        assertEqual("complexInterface: parameter constraint", s.getParameterConstraints().get(0).getRelation().toString(), "constraint");
        assertEqual("complexInterface: parameter constraint arg", s.getParameterConstraints().get(0).getParameters().get(0).getName(), "T1");
        assertEqual("complexInterface: superinterface 0", s.getSuperinterfaces().get(0).getName(), "super1");
        assertEqual("complexInterface: superinterface 1", s.getSuperinterfaces().get(1).getName(), "super2");
        assertEqual("complexInterface: placeholders", s.getPlaceholders().toString(), "[P]");

    }
    public void abstractFn()
    {
        SymbolBuilder b = new SymbolBuilder ();
        DefnVisitor v = b.beginFunction ("test");
        v.beginAbstractBody();
        v.returnType(INT);
        v.endFunctionBody();
        b.endFunction();
        FunctionSymbol s = (FunctionSymbol)b.build();
        assertEqual("abstractFunction: name", s.getName(), "test");
        assertTrue("abstractFunction: funtion should be marked as abstract", s.isAbstract());
        assertEqual("abstractFunction: class of return type", s.getReturnType().getClass(), TypeReference.class);
    }
    public void interfaceAbstractFn()
    {
        SymbolBuilder b = new SymbolBuilder ();
        b.beginInterface ("test");
        DefnVisitor im = b.visitInterfaceMember();
        DefnVisitor v = im.beginFunction("member");
        v.beginAbstractBody();
        v.returnType(INT);
        v.endFunctionBody();
        im.endFunction();
        b.endInterface();
        InterfaceSymbol s = (InterfaceSymbol)b.build();
        assertEqual("interfaceAbstractFn: entry count", s.getEntries().size(), 1);
        assertEqual("interfaceAbstractFn: entry 0 name", s.getEntries().get(0).getName(), "member");
    }
    public void function()
    {
        SymbolBuilder b = new SymbolBuilder ();
        TestExecutable testExecutable = new TestExecutable();
        PatternMatcher testPatternMatcher = new TestPatternMatcher();
        b.setExecutableBuilderFactory (TestExecutableBuilder.factoryReturning (testExecutable));
        b.setPatternMatcherBuilderFactory (TestPatternMatcherBuilder.factoryReturning (testPatternMatcher));
        DefnVisitor v = b.beginFunction ("test");
        v.parameterPattern (new Pattern.Named("a", Optional.of(INT)));
        v.beginImplementationBody();
        v.returnType(INT);
        v.bodyImplementation (new StmtVisitor.Visitable() {
            public void visit (StmtVisitor visitor) { }
        });
        v.endFunctionBody();
        b.endFunction();
        FunctionSymbol s = (FunctionSymbol)b.build();
        assertEqual("function: name", s.getName(), "test");
        assertFalse("function: funtion should not be marked as abstract", s.isAbstract());
        assertEqual("function: class of return type", s.getReturnType().getClass(), TypeReference.class);
        assertEqual("function: parameter count", s.getParameters ().size(), 1);
        assertEqual("function: parameter 0 matcher", s.getParameters().get(0), testPatternMatcher);
        assertEqual("function: executable", s.getExecutable(), Optional.of(testExecutable));

    }
    public void explicitFunctionType()
    {
        SymbolBuilder b = new SymbolBuilder ();
        TestExecutable testExecutable = new TestExecutable();
        b.setExecutableBuilderFactory (TestExecutableBuilder.factoryReturning (testExecutable));
        DefnVisitor v = b.beginFunction ("test");
        v.parameterPattern (new Pattern.Named("a", Optional.of(INT)));
        v.beginImplementationBody();
        v.returnType(INT);
        v.bodyImplementation (new StmtVisitor.Visitable() {
            public void visit (StmtVisitor visitor) { }
        });
        v.endFunctionBody();
        b.endFunction();
        assertEqual("explicitFunctionType: type", b.build().getType().get().getName(), "(int) -> int");
    }
    public void functionTypeWithImpliedPolymorphism ()
    {
        SymbolBuilder b = new SymbolBuilder ();
        TestExecutable testExecutable = new TestExecutable();
        b.setExecutableBuilderFactory (TestExecutableBuilder.factoryReturning (testExecutable));
        DefnVisitor v = b.beginFunction ("test");
        v.parameterPattern (new Pattern.Named("a", Optional.empty()));
        v.beginImplementationBody();
        v.returnType(INT);
        v.bodyImplementation (new StmtVisitor.Visitable() {
            public void visit (StmtVisitor visitor) { }
        });
        v.endFunctionBody();
        b.endFunction();
        assertEqual("functionTypeWithImpliedPolymorphism: type", b.build().getType().get().getName(), "forall _1 . (_1) -> int");
    }
    public void functionTypeWithExplicitPolymorphism ()
    {
        SymbolBuilder b = new SymbolBuilder ();
        TestExecutable testExecutable = new TestExecutable();
        b.setExecutableBuilderFactory (TestExecutableBuilder.factoryReturning (testExecutable));
        b.beginForAll();
        b.typeParameter("T1");
        DefnVisitor v = b.beginFunction ("test");
        v.parameterPattern (new Pattern.Named("a", Optional.of(T1)));
        v.parameterPattern (new Pattern.Named("b", Optional.empty()));
        v.beginImplementationBody();
        v.returnType(INT);
        v.bodyImplementation (new StmtVisitor.Visitable() {
            public void visit (StmtVisitor visitor) { }
        });
        v.endFunctionBody();
        b.endFunction();
        assertEqual("functionTypeWithExplicitPolymorphism: type", b.build().getType().get().getName(), "forall T1, _1 . (T1, _1) -> int");
    }
    public void functionWithConstraint ()
    {
        SymbolBuilder b = new SymbolBuilder ();
        TestExecutable testExecutable = new TestExecutable();
        b.setExecutableBuilderFactory (TestExecutableBuilder.factoryReturning (testExecutable));
        b.beginForAll();
        b.typeParameter("T1");
        b.typeConstraint(new Type.Constraint(
            SID.from("constraint"), 
            Collections.singletonList(new Type.NamedType(SID.from("T1")))
            ));
        
        DefnVisitor v = b.beginFunction ("test");
        v.parameterPattern (new Pattern.Named("a", Optional.of(T1)));
        v.beginImplementationBody();
        v.returnType(INT);
        v.bodyImplementation (new StmtVisitor.Visitable() {
            public void visit (StmtVisitor visitor) { }
        });
        v.endFunctionBody();
        b.endFunction();
        assertEqual("functionWithConstraint: type", b.build().getType().get().getName(), "forall T1 : constraint(T1) . (T1) -> int");
    }
}
