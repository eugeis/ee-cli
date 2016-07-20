package ee.cli.core.integ.api

import ee.cli.core.model.Item

import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.annotation.PreDestroy
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class Mail extends Item {

    String host = 'smtp.gmail.com'
    int port = 465 //465,587
    String user
    String from
    Closure<String> passwordCallback

    Properties props
    Session session
    Transport transport

    void send(String to, String subject, String message) {

        MimeMessage msg = buildMessage(to, subject)
        msg.setText(message)
        send(msg)
    }

    void send(String to, String subject, String message, File file) {

        MimeMessage msg = buildMessage(to, subject)

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(message + '\n')

        MimeBodyPart attachmentPart = buildAttachmentPart(file)

        Multipart mp = new MimeMultipart()
        mp.addBodyPart(textPart)
        mp.addBodyPart(attachmentPart)

        msg.setContent(mp)

        send(msg)
    }

    protected void send(MimeMessage msg) {
        transport().sendMessage(msg, msg.allRecipients)
    }

    private MimeBodyPart buildAttachmentPart(File file) {
        MimeBodyPart ret = new MimeBodyPart();

        FileDataSource fds = new FileDataSource(file)
        ret.setDataHandler(new DataHandler(fds));
        ret.setFileName(fds.name);
        ret
    }

    protected MimeMessage buildMessage(String to, String subject) {
        def ret = new MimeMessage(session())
        ret.setSubject(subject)
        ret.setFrom(new InternetAddress(from))
        ret.addRecipient(Message.RecipientType.TO, new InternetAddress(to))
        ret.setSentDate(new Date());

        ret
    }

    protected Transport transport() {
        if (!transport) {
            transport = session().getTransport('smtps')
            transport.connect(host, port, user, passwordCallback())
        }
        transport
    }

    @PreDestroy
    void close() {
        if (transport) {
            transport.close()
        }
    }

    protected Session session() {
        if (!session) {
            if (!user && from) {
                user = from
            }

            props = new Properties()
            props.put('mail.smtp.user', user)
            props.put('mail.smtp.host', host)
            props.put('mail.smtp.port', port)
            props.put('mail.smtp.starttls.enable', 'true')
            props.put('mail.smtp.debug', 'true')
            props.put('mail.smtp.auth', 'true')
            props.put('mail.smtp.socketFactory.port', port)
            props.put('mail.smtp.socketFactory.class', 'javax.net.ssl.SSLSocketFactory')
            props.put('mail.smtp.socketFactory.fallback', 'false')

            session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            new PasswordAuthentication(user, passwordCallback())
                        }
                    })
        }
        session
    }
}
