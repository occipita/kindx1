package kind.x1.interpreter;

import kind.x1.ast.*;
import kind.x1.*;
import kind.x1.misc.SID;
import java.util.List;
import kind.x1.interpreter.symbols.SymbolBuilder;
import kind.x1.interpreter.symbols.Symbol;

public class ModuleBuilder extends ModVisitor
{
    ModuleResolver moduleResolver = ModuleResolver.NULL;
    DiagnosticProducer diagnosticProducer = DiagnosticProducer.CONSOLE;
    Factory<SymbolBuilder> symbolBuilderFactory = SymbolBuilder.FACTORY;
    
    KindModule building = new KindModule();
    public KindModule build()
    {
        return building;
    }
    
    public void setModuleResolver (ModuleResolver r) { moduleResolver = r; }
    public void setDiagnosticProducer (DiagnosticProducer d) { diagnosticProducer = d; }
    public void setSymbolBuilderFactory (Factory<SymbolBuilder> f) { symbolBuilderFactory = f; }
    
    public void importWild (SID base, Optional<SID> as) 
    { 
        Optional<KindModule> module = moduleResolver.findModule(base);
        if (!module.isPresent()) { 
            error ("KindModule " + base + " not found");
            return;
        }
        Scope target = building.getLocalScope();
        for (Symbol s  : module.get().getExportedSymbols())
            target.addSymbol (s);
    }
    public void importSpecified (SID base, List<String> ids, Optional<SID> as) 
    { 
        Optional<KindModule> module = moduleResolver.findModule(base);
        if (!module.isPresent()) { 
            error ("KindModule " + base + " not found");
            return;
        }
        Scope target = building.getLocalScope();
        for (String id : ids) {
            Optional<Symbol> sym = module.get().getExportedSymbol (id);
            if (sym.isPresent()) {
                target.addSymbol (sym.get());
            }
            else {
                error ("Symbol "+base+"::"+id+" not found");
            }
        }
    }
    public void export (String symbol) { building.export (symbol); }
    public void define (DefnVisitor.Visitable definition) 
    {
        SymbolBuilder sb = symbolBuilderFactory.create();
        definition.visit(sb);
        Symbol sym = sb.build();
        building.getLocalScope().addSymbol(sym); 
    }
    public void defineAndExport (DefnVisitor.Visitable definition)
    {
        SymbolBuilder sb = symbolBuilderFactory.create();
        definition.visit(sb);
        Symbol sym = sb.build();
        building.getLocalScope().addSymbol(sym);
        building.export(sym.getName()); 
    }

    private void error (String msg)
    {
        diagnosticProducer.error (msg);
    }
}
