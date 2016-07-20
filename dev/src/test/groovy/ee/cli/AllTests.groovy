package ee.cli

import ee.cli.env.EnvironmentImplTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite.class)
@Suite.SuiteClasses([
        EeTest,
        MySqlServiceTest,
        MySqlToolTest,
        WildFlyServiceTest,
        WildFlyToolTest,
        EnvironmentImplTest,
        DslTest,
        BundleTest
])
class AllTests extends TestBase {
}
