package kind.x1;

import kind.x1.ast.*;
import java.io.*;
import kind.x1.interpreter.*;

public class Main
{
    public static void main(String [] args) throws IOException
    {
        long st = System.currentTimeMillis();
        
        KindParser p = new KindParser();
        
        File dir = new File("/storage/sdcard0/JavaNIDE/kindx1/app/kind/tests");
        System.out.println ("Working in kind source directory: " + dir.getAbsolutePath());
        for (File f : dir.listFiles())
        {
            if (!f.getName().endsWith(".ks")) continue;
            System.out.println("Processing: " + f);
            
            Mod mod = p.parse(f);
            if (mod == null)
            {
                System.err.println ("Parse error in "+f);
                continue;
            }
            try (Writer w = new FileWriter(f.getPath() + ".parsetree.txt"))
            {
                w.write (mod.toString());
                w.write ("\n");
            }
                        
            ModuleBuilder modBuilder = new ModuleBuilder();
            mod.visit (modBuilder);
            KindModule module = modBuilder.build();
            
            
        }
        
        System.out.println("Total time: " + (System.currentTimeMillis()-st) + "ms");
    }
}
