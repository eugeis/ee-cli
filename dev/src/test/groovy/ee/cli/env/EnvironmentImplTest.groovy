package ee.cli.env

import ee.cli.EnvironmentImplBasedTest
import ee.cli.core.model.Service
import ee.cli.core.model.Settings
import org.junit.Test

class EnvironmentImplTest extends EnvironmentImplBasedTest {

    @Test
    void testHierarchicalSettings() {
        Settings settings = env.settings(new Service().label)
        def debug = settings.get('debug')
        assert !debug

        //change local settings for the item
        settings.set('debug', true)
        debug = settings.get('debug')
        assert debug

        //the base changes are not changed
        debug = env.base.get('debug')
        assert !debug
    }
}
