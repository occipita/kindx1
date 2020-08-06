package kind.x1.interpreter.test;

import kind.x1.*;
import kind.x1.ast.*;
import kind.x1.misc.*;
import kind.x1.interpreter.*;
import kind.x1.interpreter.symbols.*;
import kind.x1.interpreter.patterns.*;

import java.util.List;
import java.util.Collections;
import java.util.Optional;

public class ModuleBuilderTest extends Assertions implements Runnable
{
    public void run()
    {
        importSpecified();
        cannotImportUnexportedSymbol();
        importWildcard();
        definition();
        exportedDefinition();
        export();
        overloadedFunction();
    }
    public void importSpecified()
    {
        ModuleBuilder b = new ModuleBuilder();
        KindModule testModule = new KindModule();
        Symbol testSymbol = new TestSymbol ("testSymbol");
        testModule.getLocalScope().addSymbol (testSymbol);
        testModule.export ("testSymbol");
        b.setModuleResolver (new StaticModuleResolver ().add(SID.from("test::module"), testModule));
        b.importSpecified (SID.from("test::module"), Collections.singletonList("testSymbol"), Optional.empty());
        KindModule built = b.build();
        
        assertEqual ("importSpecified: testSymbol should be defined in local scope", 
            built.getLocalScope().getSymbol ("testSymbol").orElse(null),
            testSymbol);
    }
    public void cannotImportUnexportedSymbol()
    {
        ModuleBuilder b = new ModuleBuilder();
        TestDiagnosticProducer tdp = new TestDiagnosticProducer();
        b.setDiagnosticProducer (tdp);
        KindModule testModule = new KindModule();
        Symbol testSymbol = new TestSymbol ("testSymbol");
        testModule.getLocalScope().addSymbol (testSymbol);
        // not done: testModule.export ("testSymbol");
        b.setModuleResolver (new StaticModuleResolver ().add(SID.from("test::module"), testModule));
        b.importSpecified (SID.from("test::module"), Collections.singletonList("testSymbol"), Optional.empty());
        KindModule built = b.build();
        
        assertEqual ("cannotImportUnexportedSymbol: testSymbol should not be defined in local scope", 
            built.getLocalScope().getSymbol ("testSymbol"),
            Optional.empty());
        assertEqual ("cannotImportUnexportedSymbol: should have produced an error message",
            tdp.getErrors().size(),
            1);
    }
    public void importWildcard()
    {
        ModuleBuilder b = new ModuleBuilder();
        KindModule testModule = new KindModule();
        Symbol testSymbol1 = new TestSymbol ("testSymbol1");
        Symbol testSymbol2 = new TestSymbol ("testSymbol2");
        Symbol testSymbol3 = new TestSymbol ("testSymbol3");
        testModule.getLocalScope().addSymbol (testSymbol1);
        testModule.getLocalScope().addSymbol (testSymbol2);
        testModule.getLocalScope().addSymbol (testSymbol3);
        testModule.export ("testSymbol1");
        testModule.export ("testSymbol2");
        b.setModuleResolver (new StaticModuleResolver ().add(SID.from("test::module"), testModule));
        b.importWild (SID.from("test::module"), Optional.empty());
        KindModule built = b.build();
        
        assertEqual ("importWildcard: testSymbol1 should be defined in local scope", 
            built.getLocalScope().getSymbol ("testSymbol1").orElse(null),
            testSymbol1);
        assertEqual ("importWildcard: testSymbol2 should be defined in local scope", 
            built.getLocalScope().getSymbol ("testSymbol2").orElse(null),
            testSymbol2);
        assertEqual ("importWildcard: testSymbol3 should not be defined in local scope", 
            built.getLocalScope().getSymbol ("testSymbol3"),
            Optional.empty());
    }
    public void definition()
    {
        ModuleBuilder b = new ModuleBuilder();
        TestSymbol sym = new TestSymbol("testSymbol");
        final TestSymbolBuilder tsb = new TestSymbolBuilder(sym);
        final boolean [] called = new boolean[1];
        
        b.setSymbolBuilderFactory ( new StaticFactory<SymbolBuilder>(tsb));
        b.define (new DefnVisitor.Visitable() {
            public void visit (DefnVisitor v) {
                assertEqual ("definition: visitor should be symbol builder", v, tsb);
                called[0] = true;
            }
        });
        assertTrue ("definition: definition.visit not called", called[0]);
        
        assertEqual ("definition: testSymbol should be defined", 
            b.build().getLocalScope().getSymbol("testSymbol"),
            Optional.of(sym));
    }
    public void exportedDefinition()
    {
        ModuleBuilder b = new ModuleBuilder();
        TestSymbol sym = new TestSymbol("testSymbol");
        final TestSymbolBuilder tsb = new TestSymbolBuilder(sym);
        final boolean [] called = new boolean[1];
        
        b.setSymbolBuilderFactory ( new StaticFactory<SymbolBuilder>(tsb));
        b.defineAndExport (new DefnVisitor.Visitable() {
            public void visit (DefnVisitor v) {
                assertEqual ("exportedDefinition: visitor should be symbol builder", v, tsb);
                called[0] = true;
            }
        });
        assertTrue ("exportedDefinition: definition.visit not called", called[0]);
        
        KindModule built = b.build();
        
        assertEqual ("exportedDefinition: testSymbol should be defined", 
            built.getLocalScope().getSymbol("testSymbol"),
            Optional.of(sym));
        assertEqual ("exportedDefinition: testSymbol should be exported", 
            built.getExportedSymbol("testSymbol"),
            Optional.of(sym));
    }
    public void export()
    {
        ModuleBuilder b = new ModuleBuilder();
        // inject a symbol to export by importing from another module:
        KindModule testModule = new KindModule();
        Symbol testSymbol = new TestSymbol ("testSymbol");
        testModule.getLocalScope().addSymbol (testSymbol);
        testModule.export ("testSymbol");
        b.setModuleResolver (new StaticModuleResolver ().add(SID.from("test::module"), testModule));
        b.importSpecified (SID.from("test::module"), Collections.singletonList("testSymbol"), Optional.empty());
        // and export it:
        b.export("testSymbol");
        
        KindModule built = b.build();
        
        assertEqual ("export: testSymbol should be exported", 
            built.getExportedSymbol ("testSymbol"),
            Optional.of(testSymbol));
    }
    public void overloadedFunction()
    {
        ModuleBuilder b = new ModuleBuilder();
        FunctionSymbol f1 = new FunctionSymbol ("testSymbol", Optional.empty()); 
        f1.getParameters().add (new TypeCheckingPatternMatcher(INT));
        f1.setReturnType(T1);
        FunctionSymbol f2 = new FunctionSymbol ("testSymbol", Optional.empty()); 
        f2.getParameters().add (new TypeCheckingPatternMatcher(T1));
        f2.setReturnType(INT);
        
        final TestSymbolBuilder tsb1 = new TestSymbolBuilder(f1);
        final TestSymbolBuilder tsb2 = new TestSymbolBuilder(f2);
        
        b.setSymbolBuilderFactory ( new StaticFactory<SymbolBuilder>(tsb1));
        b.define (EMPTYDEFN);
        b.setSymbolBuilderFactory ( new StaticFactory<SymbolBuilder>(tsb2));
        b.define (EMPTYDEFN);
        
        KindModule m = b.build();
        assertTrue ("overloadedFunction: testSymbol should be defined", 
            m.getLocalScope().getSymbol("testSymbol").isPresent());
        Symbol s =  m.getLocalScope().getSymbol("testSymbol").get();
        assertEqual ("overloadedFunction: testSymbol symbol class", s.getClass(), OverloadedFunctionSymbol.class);
        assertEqual ("overloadedFunction: testSymbol def type", s.getType().get().getName(), "{(int) -> t1, (t1) -> int}");
    }
    private final static DefnVisitor.Visitable EMPTYDEFN = new DefnVisitor.Visitable() {
            public void visit (DefnVisitor v) {
            }
        };
    private final static TestType INT = new TestType(SID.from("int"));
    private final static TestType T1 = new TestType(SID.from("t1"));
}
