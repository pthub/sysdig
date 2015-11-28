package uk.co.thinktag.monitor;


import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {

	Properties props;
	
	public MailUtil(Properties config){
		this.props = new Properties();
		props.put("mail.smtp.auth", config.getProperty("mail.smtp.auth"));
		props.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable"));
		props.put("mail.smtp.host", config.getProperty("mail.smtp.host"));
		props.put("mail.smtp.port", config.getProperty("mail.smtp.port"));
		props.put("mailer.sender", config.getProperty("mailer.sender"));
		props.put("mailer.pass", config.getProperty("mailer.pass"));
		props.put("mailer.receiver", config.getProperty("mailer.receiver"));
	}
	
	
	public void sendMail(String subject, String msg) {
		
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(props.getProperty("mailer.sender"), 
								props.getProperty("mailer.pass"));
					}
				});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(props.getProperty("mailer.sender")));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(props.getProperty("mailer.receiver")));
			message.setSubject(subject);
			message.setContent(msg,  "text/html");

			Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
