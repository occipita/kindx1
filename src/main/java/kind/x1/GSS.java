package kind.x1;

import java.util.*;

public abstract class GSS<T> implements Iterable<GSS.Entry<T>>
{
    private static class Wildcard<T> extends GSS<T>
    {
        public boolean isWildcard() { return true; }  
        public boolean equals (Object o) { return ((GSS)o).isWildcard(); }
        public int hashCode() { return 101; }
        public String toString() { return "#"; }
    }
    private static class Empty<T> extends GSS<T>
    {
        public boolean isEmpty() { return true; }
        public boolean equals (Object o) { return ((GSS)o).isEmpty(); }
        public int hashCode() { return 0; }
        public String toString() { return "$"; }
    }
    public static class Entry<T> extends GSS<T>
    {
        T head;
        GSS<T> tails;

        Entry(T h, GSS<T> t) { head = h; tails = t; }        
        public Iterator<GSS.Entry<T>> iterator() { return Collections.singleton(this).iterator(); }
        public T getHead() { return head; }
        public GSS<T> getTails() { return tails; }
        public GSS<T> pop() { return tails; }
        public boolean equals (Object o)
        {
            if (o == this) return true;
            return (o instanceof Entry) && head.equals(((Entry)o).head) && tails.equals(((Entry)o).tails);
        }
        public int hashCode () { return head.hashCode()*3 + tails.hashCode()*13; }
        public String toString() { return head + " " + tails; }
    }
    private static class EntrySet<T> extends GSS<T>
    {
        GSS.Entry<T>[] entries;
        
        EntrySet (GSS.Entry<T>[] e) { entries = e; Arrays.sort(entries, HashCodeComparator.INSTANCE); }
        public Iterator<GSS.Entry<T>> iterator() { return Arrays.asList(entries).iterator(); }    
        public GSS<T> pop() {
            GSS<T> tails = entries[0].tails;
            for (int i = 1; i < entries.length; i++)
                tails = merge(tails, entries[i].tails);
            return tails;
        }
        public boolean equals (Object o)
        {
            if (o == this) return true;
            if ((o instanceof Entry) && entries.length == 1) return entries[0].equals(o);
            return (o instanceof EntrySet) && Arrays.equals(entries, ((EntrySet)o).entries);
        }
        public int hashCode () { return Arrays.hashCode(entries); }
        public String toString() { return Arrays.toString(entries); }
    }
    public boolean isWildcard() { return false; }
    public boolean isEmpty() { return false; }
    public Iterator<GSS.Entry<T>> iterator() { return Collections.<Entry<T>>emptySet().iterator(); }
    
    public static <T> GSS<T> empty() { return new Empty<T>(); }
    public static <T> GSS<T> wildcard() { return new Wildcard<T>(); }
    public GSS<T> push (T value)
    {
        Entry<T> e = new Entry<>(value, this);
        return e;
    }
    public GSS<T> pop ()
    {
        throw new NoSuchElementException ();
    }
    
    public static <T> GSS<T> merge(GSS<T>... stacks)
    {
        // common situations where we don't need to do any work:
        if (stacks.length == 1) return stacks[0];
        if (stacks.length == 2 && stacks[0].equals(stacks[1])) return stacks[0];
        for (GSS<T> s : stacks)
            if (s.isWildcard()) return s;
            
        // otherwise, merge tails of duplicated entries:
        Map<T, GSS.Entry<T>> entries =new HashMap<>();
        for (GSS<T> s : stacks)
            for (GSS.Entry<T> e : s)
            {
                GSS.Entry<T> existing = entries.get(e.head);
                if (existing != null)
                    existing.tails = merge (existing.tails, e.tails);
                else
                    entries.put (e.head, new GSS.Entry<T>(e.head, e.tails));       
            }
 
        if (entries.size() == 1) return entries.values().iterator().next();           
        return new EntrySet<T>(entries.values().toArray(new GSS.Entry[0]));
    }
}
