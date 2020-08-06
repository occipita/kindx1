package kind.x1.interpreter.test;

import java.util.List;
import java.util.ArrayList;
import kind.x1.DiagnosticProducer;
public class TestDiagnosticProducer implements DiagnosticProducer {
    List<String> errors = new ArrayList<>();
    List<String> warnings = new ArrayList<>();
    
    public void error (String msg) { errors.add(msg); }
    public void warning (String msg) { warnings.add(msg); }
    
    public List<String> getErrors() { return errors; }
    public List<String> getWarnings() { return warnings; }
}
