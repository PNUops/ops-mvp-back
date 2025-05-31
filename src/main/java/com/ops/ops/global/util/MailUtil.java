package com.ops.ops.global.util;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailUtil {

    private final JavaMailSender javaMailSender;

    public void sendMail(final List<String> toUserList, final String subject, final String text) {
        final SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setTo(toUserList.toArray(new String[0]));
        simpleMessage.setSubject(subject);
        simpleMessage.setText(text);
        javaMailSender.send(simpleMessage);
    }
}

