package ee.cli

import ee.cli.core.model.Result
import jdk.nashorn.internal.ir.annotations.Ignore
import org.junit.BeforeClass
import org.junit.Test

class AliasTest extends TestBase {
    static Ee ee

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
    }

    @Test
    void testAliasHandler() {
        Result ret = ee.execute('alias')
        assert ret?.ok
    }

    @Test
    void testAliasTest() {
        Result ret = ee.execute('alias test')
        assert ret?.ok
    }

    @Test
    void testAliasStartSrv() {
        Result ret = ee.execute('alias startSrv')
        assert ret?.ok
    }

    @Test
    void testAliasAutomaticDetection() {
        Result ret = ee.execute('alias cg.pl.common.shared.build()')
        assert ret?.ok
    }

    @Test
    void testDefaultHandlerAliasAutomaticDetection() {
        Result ret = ee.execute('cg.pl.common.shared.build()')
        assert ret?.ok
    }

    @Test
    void testDefaultHandlerFilter() {
        Result ret = ee.execute('filter')
        assert ret?.ok
    }
}
