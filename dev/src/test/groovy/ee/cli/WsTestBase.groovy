package ee.cli

import ee.cli.core.model.Result
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

abstract class WsTestBase extends TestBase {
    static Ee ee

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
    }

    @Before
    void before() {
        System.setProperty('home', getWorkspaceHome())
    }

    @Test
    void testToolTask() {
        Result ret = ee.execute('ws')
        assert ret?.ok
    }

    @Test
    void testIsValid() {
        Result ret = ee.execute("ws ${workspaceName}.valid")
        assert ret?.ok
    }

    @Test
    void testHome() {
        Result ret = ee.execute("ws ${workspaceName}.home")
        assert ret?.ok
    }

    protected abstract String getWorkspaceName();

    protected abstract String getWorkspaceHome();
}
