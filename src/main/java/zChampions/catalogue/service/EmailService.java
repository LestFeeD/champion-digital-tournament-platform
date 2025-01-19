package zChampions.catalogue.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import zChampions.catalogue.email.EmailSender;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender {

    private  final static Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;


    @Override
    @Async
    public void send(String to, String email) {
        logger.info("Preparing to send email to: {}", to);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            messageHelper.setText(email, true);
            messageHelper.setTo(to);
            messageHelper.setSubject("Confirm your email");
            messageHelper.setFrom("axella50506@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e){
            logger.error("Failed to send email to: {}", to, e);
            throw new IllegalArgumentException("failed to send email");
        }
    }
}
