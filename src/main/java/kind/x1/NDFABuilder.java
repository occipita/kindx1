package kind.x1;

import java.util.*;

public class NDFABuilder 
{
    private class State
    {
        int index;
        List<Link> links = new ArrayList<>();
        int mode;
        int emit;
    }
    
    private class Link
    {
        int a, b;
        State target;
        
        Link (int a, int b, State target) { this.a = a; this.b = b; this.target = target; }
    }
    public class LinkBuilder 
    {
        State[] from;
        State[] to;
        public LinkBuilder range (char min, char max)
        {
            for (int i = 0; i < from.length; i++)
                from[i].links.add (new Link ((int)min, (int)max, to[i]));
            return this;
        }
        public LinkBuilder rangeIns (char min, char max)
        {
            if (Character.toUpperCase(min) != Character.toLowerCase(min))
                return range(Character.toUpperCase(min), Character.toUpperCase(max))
                        .range(Character.toLowerCase(min), Character.toLowerCase(max));
            return range(min, max);
        }
        public LinkBuilder ch (char ch) { return range (ch, ch); }
        public LinkBuilder ins (char c) 
        {
            if (Character.toUpperCase(c) != Character.toLowerCase(c))
                return ch(Character.toUpperCase(c)).ch(Character.toLowerCase(c));
            return ch(c);
        }
        public LinkBuilder special (int include, int exclude)
        {
            for (int i = 0; i < from.length; i++)
                from[i].links.add (new Link (include, exclude, to[i]));
            return this;
        }
        public LinkBuilder special (int include) { return special (include, 0); }
        public NDFABuilder done () { focus = to[0]; return NDFABuilder.this; }
    }        
    
    List<State> states = new ArrayList<>();
    State focus;
    Map<String,State> labels = new TreeMap<>();
    State start;
    int maxStates = 8;
    
    public NDFABuilder newState()
    {
        State s = new State();
        s.index = states.size();
        states.add(s);
        focus = s;
        return this;
    }
    public NDFABuilder label (String label)
    {
        labels.put (label, focus);
        return this;
    }
    public NDFABuilder newState(String label) { return newState().label(label); }
    public NDFABuilder start() { start = focus; return this; }
    public NDFABuilder from (String label) 
    { 
        focus = labels.get(label);
        if (focus == null) throw new IllegalArgumentException ("Label " + label + " not defined");
        return this;
    }
    public NDFABuilder accept (int token)
    {
        focus.mode = NDFA.EAGER;
        focus.emit = token;
        return this;
    }
    public NDFABuilder lazyAccept (int token)
    {
        focus.mode = NDFA.LAZY;
        focus.emit = token;
        return this;
    }
    public LinkBuilder onAnyOf ()
    {
        LinkBuilder r =new LinkBuilder();
        r.from = new State[] { focus };
        newState();
        r.to = new State[] { focus };
        return r;
    }
    public LinkBuilder linkTo(String label)
    {
        LinkBuilder r =new LinkBuilder();
        r.from = new State[] { focus };
        r.to = new State[] { labels.get(label) };
        if (r.to[0] == null) throw new IllegalArgumentException ("Label " + label + " not defined");
        return r;
    }   
    public LinkBuilder linkToSelf ()
    {
        LinkBuilder r =new LinkBuilder();
        r.from = new State[] { focus };
        r.to = r.from;
        return r;
    }   
    public LinkBuilder oneOrMore ()
    {
        LinkBuilder r =new LinkBuilder();
        State s1 = focus;
        newState();
        r.from = new State[] { s1, focus };
        r.to = new State[] { focus, focus };
        return r;
    }
    public NDFABuilder onRange (char min, char max)
    {
        return onAnyOf().range(min, max).done();
    }
    public NDFABuilder onRangeIns (char min, char max)
    {
        return onAnyOf().rangeIns(min, max).done();
    }
    public NDFABuilder on (char c)
    {
        return onAnyOf().ch (c).done();
    }
    public NDFABuilder onIns (char c)
    {
        return onAnyOf().ins (c).done();
    }
    public NDFABuilder onSpecial (int include, int exclude)
    {
        return onAnyOf().special (include, exclude).done();
    }
    public NDFABuilder onSpecial (int include)
    {
        return onAnyOf().special (include).done();
    }                
    public NDFABuilder onStr (String str)
    {
        for (int i = 0; i < str.length(); i++)
            on (str.charAt(i));
        return this;
    }
    public NDFABuilder maxStates (int m) 
    {
        maxStates = m;
        return this;
    }
        
    public NDFA build ()
    {
        int [][] stateMap = new int[states.size()][];
        for (int i = 0; i < stateMap.length; i++)
        {
            State s = states.get(i);
            int [] st = new int[2 + 3*s.links.size()];
            stateMap[i] = st;
            st[0] = s.mode;
            st[1] = s.emit;
            for (int j = 0; j < s.links.size(); j++)
            {
                Link l = s.links.get(j);
                st[2+j*3] = l.a;
                st[3+j*3] = l.b;
                st[4+j*3] = l.target.index;
            }
        }
        return new NDFA(stateMap, maxStates, start.index);
    }
}
