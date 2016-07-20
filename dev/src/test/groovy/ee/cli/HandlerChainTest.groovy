package ee.cli

import ee.cli.core.model.Result
import org.junit.BeforeClass
import org.junit.Test

class HandlerChainTest extends TestBase {
    static Ee ee

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
    }

    @Test
    void testFilterBuild() {
        Result ret = ee.execute('env dev.flags(filter) dsl cg.pl.watchdog.build(install)')
        assert ret?.ok
    }

    @Test
    void testNopDeploy() {
        Result ret = ee.execute('env flags(nop) cg.pl.common.deploy')
        assert ret?.ok
    }

    @Test
    void testFilterAsAliasBuild() {
        Result ret = ee.execute('filter ttm.build(install)')
        assert ret?.ok
    }
}
