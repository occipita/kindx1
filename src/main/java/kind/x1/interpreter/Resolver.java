package kind.x1.interpreter;

import kind.x1.misc.SID;
import kind.x1.*;
import kind.x1.interpreter.symbols.Symbol;
import java.util.Optional;
import java.util.function.Function;

public abstract class Resolver 
{ 
    public Optional<Symbol> resolve(SID id) 
    { 
        String search = id.head();
        Optional<SID> sub = id.tail();
        Function<Symbol,Optional<Symbol>> subres = sub.isPresent() ? new SubResolver(sub.get()) : Mappers.mapToOptional();
        return resolve(search, subres);
    }

    public abstract Optional<Symbol> resolve(String id, Function<Symbol, Optional<Symbol>> subres);
    
    public static final Resolver EMPTY = new Resolver()
    {
        public Optional<Symbol> resolve(SID id) { return Optional.empty(); }
        public Optional<Symbol> resolve(String id, Function<Symbol, Optional<Symbol>> subres) { return Optional.empty(); }
    };
    
    static final class ScopeResolver extends Resolver
    {
        private Scope[] scopes;
        private Resolver chain;
        
        public ScopeResolver (Scope [] scopes, Resolver chain)
        {
            this.scopes = scopes;
            this.chain = chain;
        } 
        public Optional<Symbol> resolve(String search, Function<Symbol, Optional<Symbol>> subres) 
        {
            for (Scope s : scopes)
            {
                Optional<Symbol> sym = s.getSymbol(search).flatMap(subres);
                if (sym.isPresent()) return sym;
            }       
            return chain.resolve(search,subres); 
        }
    }
    public static Resolver newScope (Resolver chain, Scope... scopes) { return new ScopeResolver(scopes, chain); }
    public static class SubResolver implements Function<Symbol, Optional<Symbol>>
    {
        private String search;
        private Function<Symbol,Optional<Symbol>> subres;
        public SubResolver (SID id)
        {
            search = id.head();
            Optional<SID> sub = id.tail();
            subres = sub.isPresent() ? new SubResolver(sub.get()) : Mappers.mapToOptional();       
        }
        public Optional<Symbol> apply (Symbol s)
        {
            Optional<Resolver> r = s.getValue().getStaticMemberResolver();
            if (!r.isPresent()) return Optional.empty();
            return r.get().resolve(search,subres);
        }
    }
}
