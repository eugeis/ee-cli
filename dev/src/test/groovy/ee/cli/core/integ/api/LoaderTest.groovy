package ee.cli.core.integ.api

import ee.cli.TestBase
import jdk.nashorn.internal.ir.annotations.Ignore
import org.junit.BeforeClass
import org.junit.Test

class LoaderTest extends TestBase {

    static Loader loader

    @BeforeClass
    static void beforeClass() {
        loader = new Loader()
        loader.init()
    }

    @Test
    @Ignore
    void testLoadInstaller() {
        def file = new File('D:\\CG\\src\\cg-product\\cg-product-assembly-client\\target\\cg-product-assembly-client-dev-SNAPSHOT.jar')
        def obj = loader.parseConfig('installer.gradle', file)
        assert obj
    }

    @Test
    @Ignore
    void testLoadPrepareConfig() {
        def file = new File('D:/CG/src/cg-product/cg-product-pl-env_ear_base/target/cg-product-pl-env_ear_base-dev-SNAPSHOT.ear')
        def obj = loader.parseConfig('prepareConfig.gradle', file)
        assert obj
    }
}
