package ee.cli

import ee.cli.core.model.Result
import org.junit.BeforeClass
import org.junit.Test

abstract class ServiceTestBase extends TestBase {
    static Ee ee

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
    }

    @Test
    void testServiceTask() {
        Result ret = ee.execute('service')
        assert ret?.ok
    }

    @Test
    void testPing() {
        Result ret = ee.execute("service ${serviceName}.ping")
        assert ret?.ok
    }

    @Test
    void testStart() {
        Result ret = ee.execute("service ${serviceName}.start")
        assert ret?.ok
    }

    @Test
    void testStop() {
        Result ret = ee.execute("service ${serviceName}.stop")
        assert ret?.ok
    }

    protected abstract String getServiceName()
}
