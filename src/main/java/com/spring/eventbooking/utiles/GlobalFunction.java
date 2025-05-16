package com.spring.eventbooking.utiles;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class GlobalFunction {

    private static MessageSource ms;

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
