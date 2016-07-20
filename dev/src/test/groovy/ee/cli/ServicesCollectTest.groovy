package ee.cli

import ee.cli.core.model.Result
import org.junit.BeforeClass
import org.junit.Test

class ServicesCollectTest extends TestBase {
    static Ee ee

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
    }

    @Test
    void testServiceCopyLogs() {
        Result ret = ee.execute('service copyLogs(d:/temp)')
        assert ret?.ok
    }
}
