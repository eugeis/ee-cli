package ee.cli

import ee.cli.core.model.Result
import org.junit.BeforeClass
import org.junit.Test

abstract class PkgTestBase extends TestBase {
    static Ee ee

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
    }

    @Test
    void testPkgTask() {
        Result ret = ee.execute('pkg')
        assert ret?.ok
    }

    @Test
    void testInstall() {
        Result ret = ee.execute("pkg ${pkgName} install")
        assert ret?.ok
    }

    @Test
    void testUninstall() {
        Result ret = ee.execute("pkg ${pkgName} uninstall")
        assert ret?.ok
    }

    @Test
    void testUninstallAll() {
        Result ret = ee.execute("pkg ${pkgName} uninstallAll")
        assert ret?.ok
    }

    protected abstract String getPkgName()
}
