package ee.cli

import ee.cli.core.model.Result
import jdk.nashorn.internal.ir.annotations.Ignore
import org.junit.BeforeClass
import org.junit.Test

class ComponentTest extends DslTest {

    @Test
    void testDslBuild() {
        Result ret = ee.execute('dsl cg.pl.common.build')
        assert ret?.ok
    }

    @Test
    void testDslDeploy() {
        Result ret = ee.execute('dsl cg.pl.deploy')
        assert ret?.ok
    }

    @Test
    @Ignore
    void testDslGenerate() {
        Result ret = ee.execute('dsl cg.generate')
        assert ret?.ok
    }
}
