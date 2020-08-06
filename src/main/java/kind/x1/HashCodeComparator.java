package kind.x1;
import java.util.Comparator;
public class HashCodeComparator implements Comparator<Object>
{
    public static final HashCodeComparator INSTANCE = new HashCodeComparator();
    
    public int compare(Object o1, Object o2) { return Integer.compare(o1.hashCode(), o2.hashCode()); }
}
