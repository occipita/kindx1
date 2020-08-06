package kind.x1.misc;

import java.util.*;

public class SID 
{
    private final List<String> ids;
    
    public SID(List<String> i) { ids = Collections.unmodifiableList(i); }
    public List<String> getIds() { return ids; }
    public int hashCode () { return ids.hashCode(); }
    public boolean equals (Object o) {
        if (!(o instanceof SID)) return false;
        return ((SID)o).ids.equals(ids);
    }
    public String head() { return ids.get(0); }
    public Optional<SID> tail()
    {
        if (ids.size() == 1) return Optional.empty();
        return Optional.of (new SID(ids.subList(1, ids.size())));
    }
    public SID append (String element)
    {
        String [] elements = new String[ids.size()+1];
        ids.toArray(elements);
        elements[ids.size()] = element;
        return new SID(Arrays.asList(elements));
    }
    public String toString() { 
        StringBuilder sb = new StringBuilder();
        String s = "";
        for (String id : ids) {
            sb.append(s).append(id);
            s = "::";
        }
        return sb.toString();
    }
    public static SID from (String s)
    {
        return new SID(Arrays.asList(s.split("::"))); // FIXME low performance
    }
}
