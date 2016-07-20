package ee.cli

import ee.cli.core.StaticExtensions
import ee.cli.core.model.EnvironmentImpl
import org.junit.BeforeClass

abstract class EnvironmentImplBasedTest extends TestBase {
    static EnvironmentImpl env

    @BeforeClass
    static void beforeEnvBasedTest() {
        new StaticExtensions().extend()
        env = new EnvironmentImpl()
    }
}
