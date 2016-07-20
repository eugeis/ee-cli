package ee.cli

import ee.cli.core.model.Result
import org.junit.BeforeClass
import org.junit.Test

class EeTest extends TestBase {
    static Ee ee

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
    }

    @Test
    void testAgentHandler() {
        Result ret = ee.execute('agent')
        assert ret?.ok
    }

    @Test
    void testTestHandler() {
        Result ret = ee.execute('test')
        assert ret?.ok
    }

    @Test
    void testServiceHandler() {
        Result ret = ee.execute('service')
        assert ret?.ok
    }

    @Test
    void testBundleHandler() {
        Result ret = ee.execute('bundle')
        assert ret?.ok
    }

    @Test
    void testEnvHandler() {
        Result ret = ee.execute('env')
        assert ret?.ok
    }

    @Test
    void testToolHandler() {
        Result ret = ee.execute('tool')
        assert ret?.ok
    }

    @Test
    void testPkgHandler() {
        Result ret = ee.execute('pkg')
        assert ret?.ok
    }

    @Test
    void testWsHandler() {
        Result ret = ee.execute('ws')
        assert ret?.ok
    }
}
