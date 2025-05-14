package com.spring.eventbooking.mail.template;

import com.spring.eventbooking.entity.User;

public class WelcomeEmailContext extends AbstractEmailContext {

    private String token;

    @Override
    public <T> void init(T context) {
        T entity = (T) context;
        if (entity instanceof User user) {
            put("User", user.getFirstName());
            setTo(user.getEmail());
        }
        setTemplateLocation("mail");
        setSubject("Welcome for your registration");
        setTemplateLocation("/welcome-email");
        setFrom("no-reply@Event.com");

    }

}
