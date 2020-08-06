package kind.x1;

public class Token 
{
    public final static Token ERROR = new Token(-1,"");

    private final int type;
    private final String text;
    
    public Token(int type, String text)
    {
        this.type = type;
        this.text = text;
    }
    
    public int type () { return type; }    
    public String text () { return text; }
    
    public String toString() { return type>=0 ? TokenType.getName(type)+"("+text+")" : "ERROR"; }
}
