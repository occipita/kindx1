package kind.x1;

import java.lang.reflect.Field;
import static java.lang.reflect.Modifier.*;
import java.util.Map;
import java.util.HashMap;

public abstract class TokenType
{
    public static final int EOF = 0;
    public static final int WS = 1;
    public static final int COMMENT = 2;
    public static final int HEXLITERAL = 3;
    public static final int BINLITERAL = 4;
    public static final int OCTLITERAL = 5;
    public static final int INTLITERAL = 6;
    public static final int FLOATLITERAL = 7;
    public static final int STRINGLITERAL = 8;
    public static final int UNDERSCORE = 9;
    public static final int ID = 10;
    public static final int MINUS = 11;
    public static final int DOUBLEPLUS = 12;
    public static final int DOUBLEMINUS = 13;
    public static final int OP_ASSIGN = 14;
    public static final int OP_CONDL = 15;
    public static final int OP_CONDR = 16;
    public static final int OP_CHAIN = 17;
    public static final int OP_LOG_P = 18;
    public static final int OP_LOG_S = 19;
    public static final int OP_COMP = 20;
    public static final int OP_MISC = 21;
    public static final int OP_ARITH_I = 22;
    public static final int OP_ARITH_SH = 23;
    public static final int OP_ARITH_P = 24;
    public static final int OP_ARITH_S = 25;
    public static final int OP_BIT_P = 26;
    public static final int OP_BIT_S = 27;
    public static final int OP_PRE = 28;
    public static final int OP_POST = 29;
    public static final int COLON = 30;
    public static final int LPAREN = 31;
    public static final int RPAREN = 32;
    public static final int LSQBR = 33;
    public static final int RSQBR = 34;
    public static final int LBRACE = 35;
    public static final int RBRACE = 36;
    public static final int SEMICOLON = 37;
    public static final int DOT = 38;
    public static final int BACKSLASH = 39;
    public static final int COMMA = 40;
    public static final int ARROWL = 41;
    public static final int ARROWR = 42;
    public static final int ARROWRD = 43;
    public static final int EQUAL = 44;
    public static final int KW_IF = 45;
    public static final int KW_CLASS = 46;    
    public static final int KW_RETURN = 47;    
    public static final int KW_WHILE = 48;    
    public static final int KW_ELSE = 49;
    public static final int KW_PUBLIC = 50;
    public static final int KW_PRIVATE = 51;
    public static final int KW_PROTECTED = 52;
    public static final int KW_STATIC = 53;
    public static final int KW_ABSTRACT = 54;
    public static final int KW_FINAL = 55;
    public static final int KW_EXPORT = 56;
    public static final int KW_XATTRIB = 57;  // reserved for future expansion of the attribute id range
    public static final int KW_INTERFACE = 58;
    public static final int KW_IMPLEMENTATION = 59;
    public static final int KW_FORALL = 60;
    public static final int KW_USING = 61;
    public static final int KW_FOR = 62;
    public static final int KW_THROW = 63;
    public static final int KW_BREAK = 64;
    public static final int KW_CONTINUE = 65;
    public static final int KW_SWITCH = 66;
    public static final int KW_OPERATOR = 67;
    public static final int KW_IMPORT = 68;
    public static final int DBLCOLON = 69;
    public static final int KW_FROM = 70;
    public static final int KW_AS = 71;
    public static final int ASTERISK = 72; 
       
    private static final int MAX = 72;
    
    private static Map<String,Integer> typesByName;
    private static String[] typeNames;
    
    private static void initTables ()
    {
        typeNames = new String[MAX+1];
        typesByName = new HashMap<>();
        for (Field f : TokenType.class.getDeclaredFields ())
        {
            if (f.getModifiers() != (PUBLIC|STATIC|FINAL)) continue;
            try 
            {
                int ttype = (Integer)f.get(null);
                typeNames[ttype] = f.getName();
                typesByName.put (f.getName(), ttype);
            }
            catch(IllegalAccessException ignored)
            {
            }
        }
    }
           
    public static int fromName (String name)
    {
        if (typesByName == null) initTables();
        Integer r = typesByName.get(name);
        if (r == null) return -1;
        return (int)r;
    }
    
    public static String getName(int ttype)
    {
        if (typesByName == null) initTables();
        if (ttype< 0 || ttype > MAX) return "UNKNOWN_TOKEN_TYPE_"+ttype;
        return typeNames[ttype];
    }
    
    public static int getMax() { return MAX; }
    
    public static void main (String[] args)
    {
        for (int i = 0; i <= MAX;i++)
            System.out.println(""+i+": "+getName(i)+(fromName(getName(i))==i?"":" (REV LOOKUP FAIL)"));
    }
}
    
    