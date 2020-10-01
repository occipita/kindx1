package kind.x1;

import kind.x1.ast.*;
import java.io.*;
import java.util.Optional;
import java.util.Collections;
import kind.x1.interpreter.*;
import kind.x1.interpreter.symbols.Symbol;
import kind.x1.interpreter.symbols.FunctionSymbol;
import kind.x1.interpreter.executables.Executable;
import kind.x1.interpreter.patterns.PatternNotMatchedException;

public class Main
{
    public static void main(String [] args) throws IOException
    {
        long st = System.currentTimeMillis();
        
        KindParser p = new KindParser();

	if (args.length > 0)
	{
	    for (String a : args)
		process (p, new File(a));
	}
	else
	{
	    File dir = new File("./kind/tests");
	
	    System.out.println ("Working in kind source directory: " + dir.getAbsolutePath());
	    for (File f : dir.listFiles())
	    {
		if (!f.getName().endsWith(".ks")) continue;
		process(p, f);
	    }
	}
        System.out.println("Total time: " + (System.currentTimeMillis()-st) + "ms");
    }
    public static void process (KindParser p, File f) throws IOException
    {
	System.out.println("Processing: " + f);
            
	Mod mod = p.parse(f);
	if (mod == null)
	{
	    System.err.println ("Parse error in "+f);
	    return;
	}
	try (Writer w = new FileWriter(f.getPath() + ".parsetree.txt"))
	{
	    w.write (mod.toString());
	    w.write ("\n");
	}
                        
	ModuleBuilder modBuilder = new ModuleBuilder();
	mod.visit (modBuilder);
	KindModule module = modBuilder.build();
            
        for (Symbol symbol : module.getExportedSymbols())
	{
	    System.out.println ("  - " + symbol.getName() + " : " + symbol.getType().map(t -> t.getName()).orElse("(type unknown)"));
	}
		
        Optional<Symbol> mainSym = module.getExportedSymbol("main");
	if (!mainSym.isPresent() || !(mainSym.get() instanceof FunctionSymbol))
	{
	    System.out.println("  Module has no main function, skipping");
	    return;
	    // FIXME this doesn't handle overloaded functions
	}
	FunctionSymbol mainFn = (FunctionSymbol)mainSym.get();
	Executable mainExec = mainFn.getExecutable().orElse(Executable.NULL_EXECUTABLE);
	System.out.println ("  Main executable: " + mainExec);
	try {
	    Resolver resolver = Resolver.newScope (Resolver.EMPTY,
						   module.getLocalScope(), 
						   mainFn.generateParameterScope(Collections.emptyList()));
	    Continuation.executeUntilExit (resolver, new ExecutionContext(), mainExec);
	}
	catch (PatternNotMatchedException e) {
	    System.err.println (e);
	}
    }
}
