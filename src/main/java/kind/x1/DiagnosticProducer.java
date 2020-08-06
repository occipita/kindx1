package kind.x1;

public interface DiagnosticProducer 
{
    void error (String msg);
    void warning (String msg);
    
    public static final DiagnosticProducer NULL = new DiagnosticProducer() {
        public void error (String msg) { }
        public void warning (String msg) { }
    };
    public static final DiagnosticProducer CONSOLE = new DiagnosticProducer() {
        public void error (String msg) { }
        public void warning (String msg) { }
    };   
}
