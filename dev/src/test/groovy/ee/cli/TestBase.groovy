package ee.cli

import ee.cli.core.StaticExtensions
import org.junit.BeforeClass
import org.slf4j.ext.XLogger
import org.slf4j.ext.XLoggerFactory

class TestBase {
    final XLogger log = XLoggerFactory.getXLogger(this.class)

    @BeforeClass
    static void beforeClassBaseTest() {
        new StaticExtensions().extend()
        //System.setProperty('nop', 'true')
        System.setProperty('home', 'D:/CG')
        //System.setProperty('home', 'F:\\views\\temp\\ws')
    }
}
