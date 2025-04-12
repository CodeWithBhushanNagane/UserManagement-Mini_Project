package com.usermanagement.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtils {

	@Autowired
	private JavaMailSender mailSender;

	public boolean sendEmail(String to, String subject, String body) {
		boolean isMailSend = false;
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);

			messageHelper.setTo(to);
			messageHelper.setSubject(subject);
			messageHelper.setText(body, true);

			mailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return isMailSend;
	}
}
