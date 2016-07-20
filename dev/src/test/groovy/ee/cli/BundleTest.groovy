package ee.cli

import ee.cli.core.model.Result
import org.junit.BeforeClass
import org.junit.Test

class BundleTest extends TestBase {
    static Ee ee

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
    }

    @Test
    void testBundleHandler() {
        Result ret = ee.execute('bundle')
        assert ret?.ok
    }

    @Test
    void testBundleAdd() {
        Result ret = ee.execute('bundle add(name:dev,type:component,model:cg)')
        assert ret?.ok
    }

    @Test
    void testBundleWoNameAdd() {
        Result ret = ee.execute('bundle add(withoutName,type:component,model:cg)')
        assert ret?.ok
    }

    @Test
    void testBundleDelete() {
        Result ret = ee.execute('bundle remove(dev)')
        assert ret?.ok
    }

    @Test
    void testBundleWoNameDelete() {
        Result ret = ee.execute('bundle remove(withoutName)')
        assert ret?.ok
    }

    @Test
    void testBundlesShow() {
        Result ret = ee.execute('bundle show')
        assert ret?.ok
    }

    @Test
    void testBundlesShowAsCallable() {
        Result ret = ee.execute('bundle.show')
        assert ret?.ok
    }


    @Test
    void testBundleLoadByIndex() {
        Result ret = ee.execute('bundle 1.load')
        assert ret?.ok
    }

    @Test
    void testBundleLoad() {
        Result ret = ee.execute('bundle dev.load')
        assert ret?.ok
    }

    @Test
    void testBundleShow() {
        Result ret = ee.execute('bundle dev.show')
        assert ret?.ok
    }

    @Test
    void testBundleBuild() {
        Result ret = ee.execute('bundle dev.build')
        assert ret?.ok
    }
}
