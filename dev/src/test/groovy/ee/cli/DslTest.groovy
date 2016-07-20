package ee.cli

import ee.cli.core.model.Result
import org.junit.BeforeClass
import org.junit.Test

class DslTest extends TestBase {
    static Ee ee

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
    }

    @Test
    void testDslHandler() {
        Result ret = ee.execute('dsl')
        assert ret?.ok
    }

    @Test
    void testDslAddItem() {
        Result ret = ee.execute('dsl add(name:cgTest,type:component,file:\\src\\cg-pl\\cg-pl-ee-shared\\data\\models\\cg-model.gradle)')
        assert ret?.ok
    }

    @Test
    void testDslLoad() {
        Result ret = ee.execute('dsl cg.load')
        assert ret?.ok
    }

    @Test
    void testDslShow() {
        Result ret = ee.execute('dsl cg.show')
        assert ret?.ok
    }

    @Test
    void testDslCommonSharedBuild() {
        Result ret = ee.execute('dsl cg.pl.watchdog.build(install)')
        assert ret?.ok
    }
}
