package com.lambdatech.learncamelspringboot.alert;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MailProcessor implements Processor {

    @Autowired
    JavaMailSender emailSender;

    @Autowired
    Environment environment;

    @Override
    public void process(Exchange exchange) throws Exception {

        Exception e= exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        log.info("Exception Caught in mail processor : " + e.getMessage());

        String messageBody = "Exception happened in the camel Route: "+e.getMessage();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(environment.getProperty("mailFrom"));
        message.setTo(environment.getProperty("mailto"));
        message.setSubject("Exception in Camel Route");
        message.setText(messageBody);

        emailSender.send(message);
    }
}
