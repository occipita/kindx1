package kind.x1;

import org.junit.*;
import static org.junit.Assert.*;

public class MiscTest
{
    @Test
    public void runLegacyUnitTests()
    {
	RunUnitTests.main(new String[0]);
    }
    @Test
    public void runLegacyInterpreterTests()
    {
	RunInterpreterTests.main(new String[0]);
    }
}
