package org.fs.rallyroundbackend.event;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fs.rallyroundbackend.entity.users.participant.EmailVerificationTokenEntity;
import org.fs.rallyroundbackend.entity.users.participant.ParticipantEntity;
import org.fs.rallyroundbackend.repository.user.EmailVerificationTokenRepository;
import org.fs.rallyroundbackend.repository.user.ParticipantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class OnRegistrationRequestEventListener implements ApplicationListener<OnRegistrationRequestEvent> {

    private final MessageSource messageSource;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JavaMailSender mailSender;
    private final ParticipantRepository participantRepository;
    @Value("${rr.email.account}")
    private String fromEmailAddress;

    @Override
    @Transactional
    @Async
    public void onApplicationEvent(OnRegistrationRequestEvent event) {
        Random random = new Random();
        // Generate the code to verify the email a random number of 6 digits.
        int code = 100000 + random.nextInt(900000);

        ParticipantEntity participantEntity = participantRepository.findById(event.getUser())
                .orElseThrow(EntityNotFoundException::new);

        // Create and save email verification token.
        emailVerificationTokenRepository.save(
          EmailVerificationTokenEntity.builder()
                  .user(participantEntity)
                  .code(code)
                  .build()
        );

        // Get the correct message text and subject based on the client's language and region.
        String mailText = messageSource.getMessage("email.completeRegistration", null, event.getLocale());
        String mailSubject = messageSource.getMessage("email.completeRegistration.subject", null,
                event.getLocale());

        // Set the generated email verification code to the mail text.
        mailText = String.format(mailText, code);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setSubject(mailSubject);
            helper.setTo(participantEntity.getEmail());
            helper.setFrom(fromEmailAddress);
            helper.setText(mailText, true);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        log.info("Sending email with the verification code.");
        mailSender.send(mimeMessage);
        log.info("Email with the verification code sended.");
    }
}
