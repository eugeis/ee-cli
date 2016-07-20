package ee.cli.core.integ

import ee.cli.TestBase
import ee.cli.core.model.Command
import ee.cli.core.model.Workspace
import org.junit.BeforeClass
import org.junit.Test

class CommandFactoryTest extends TestBase {
    private static CommandFactory factory

    @BeforeClass
    static void beforeClass() {
        factory = new CommandFactory()
    }

    @Test
    void testSimpleCommand() {
        Command command = factory.parse('cg.load')
        assert commnd
    }

    @Test
    void testPathCommand() {
        Command command = factory.parse('cg.pl.common.shared.build')
        assert commnd
    }
}
