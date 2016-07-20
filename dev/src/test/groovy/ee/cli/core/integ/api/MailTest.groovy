package ee.cli.core.integ.api

import ee.cli.TestBase
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

class MailTest extends TestBase {

    static Mail mail
    static String email

    @BeforeClass
    static void beforeClass() {
        email = 'eoeisler@gmail.com'
        mail = new Mail(from: email, passwordCallback: { 'xxx' } )
    }


    @AfterClass
    static void afterClass() {
        mail.close()
    }

    @Test
    void testSendTextMessage() {
        mail.send(email, 'test1', 'test1')
        mail.send(email, 'test2', 'test2')
    }

    @Test
    void testSendAttachmentMessage() {
        def file = new File('Test.txt')
        mail.send(email, 'test1', 'test1', file)
        mail.send(email, 'test2', 'test2', file)
    }
}
