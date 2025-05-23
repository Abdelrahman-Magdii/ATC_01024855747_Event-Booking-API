package com.spring.eventbooking.utiles;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class GlobalFunction {

    public static MessageSource ms;

    public GlobalFunction(MessageSource messageSource) {
        GlobalFunction.ms = messageSource;
    }

    public static String getMS(String message, long userId) {
        return ms.getMessage(message, new Object[]{userId}, LocaleContextHolder.getLocale());
    }

    public static String getMS(String message, String name) {
        return ms.getMessage(message, new Object[]{name}, LocaleContextHolder.getLocale());
    }

    public static String getMS(String message) {
        return ms.getMessage(message, null, LocaleContextHolder.getLocale());
    }
}
