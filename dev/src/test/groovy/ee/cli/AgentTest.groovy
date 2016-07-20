package ee.cli

import ee.cli.handler.AgentHandler
import org.junit.BeforeClass
import org.junit.Test

class AgentTest extends TestBase {
    static Ee ee
    static AgentHandler agent

    @BeforeClass
    static void beforeClass() {
        ee = new Ee()
        agent = new AgentHandler(ee: ee)
    }

    @Test
    void testStart() {
        agent.start()
    }

    @Test
    void testStop() {
        agent.stop()
    }
}
