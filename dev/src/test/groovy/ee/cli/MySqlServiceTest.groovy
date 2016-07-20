package ee.cli

import org.junit.Before

class MySqlServiceTest extends ServiceTestBase {

    @Before
    void before() {
        //System.setProperty('nop', 'false')
    }

    @Override
    protected String getServiceName() {
        return 'mySql'
    }
}