package ee.cli

import ee.cli.core.model.Result
import org.junit.BeforeClass
import org.junit.Test

abstract class ToolTestBase extends TestBase {
    static Ee ee

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
    }

    @Test
    void testToolTask() {
        Result ret = ee.execute('tool')
        assert ret?.ok
    }

    @Test
    void testHome() {
        Result ret = ee.execute("tool ${toolName}.home")
        assert ret?.ok
    }

    protected abstract String getToolName();
}
