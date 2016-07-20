package ee.cli.core.integ

import ee.cli.EnvironmentImplBasedTest
import ee.cli.core.integ.api.Runner
import org.junit.Before
import org.junit.Test

class InjectorTest extends EnvironmentImplBasedTest {
    InjectorImpl injector

    @Before
    void beforeInjectorTest() {
        injector = new InjectorImpl(env: EnvironmentImplBasedTest.env)
    }

    @Test
    void testClassBasedTask() {
        ClassBasedHandler item = injector.resolve(ClassBasedHandler)
        assert item
    }

    @Test
    void testRunner() {
        Runner item = injector.resolve(Runner)
        assert item
    }
}
