package ee.cli

import ee.cli.core.model.Result
import org.junit.BeforeClass
import org.junit.Test

class EnvTest extends TestBase {
    static Ee ee

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
    }

    @Test
    void testEnvTask() {
        Result ret = ee.execute('env')
        assert ret?.ok
    }

    @Test
    void testFlag() {
        Result ret = ee.execute("env ${envName}.flags(filter,update)")
        assert ret?.ok
    }

    @Test
    void testDefaultEnvFlag() {
        Result ret = ee.execute("env flags(filter,update)")
        assert ret?.ok
    }

    @Test
    void testParam() {
        Result ret = ee.execute("env ${envName}.mvn.params(home:D:/src,nop:true)")
        assert ret?.ok
    }

    @Test
    void testEnvAddItem() {
        Result ret = ee.execute("env add(name:dev2,type:common)")
        assert ret?.ok
    }

    protected String getEnvName() { 'dev' }
}
