package ee.cli.core.integ

import ee.cli.TestBase
import ee.cli.core.model.Workspace
import org.junit.BeforeClass
import org.junit.Test

class DependencyResolverTest extends TestBase {

    static DependencyResolver resolver

    @BeforeClass
    static void beforeClass() {
        resolver = new DependencyResolver(workspace: new Workspace(home: '/Users/eugeis/Temp/ws'))
        resolver.init()
    }

    @Test
    void testResolve() {
        //def ret = resolver.resolve('junit:junit:4.12')
        def ret = resolver.resolve('org.springframework:spring-core:4.2.5.RELEASE')
        println "Resolved $ret"
    }
}
