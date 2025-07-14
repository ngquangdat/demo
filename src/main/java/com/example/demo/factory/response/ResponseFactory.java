package com.example.demo.factory.response;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import java.util.Locale;


public class ResponseFactory {

    private static ResourceBundleMessageSource messageSource;

    static {
        ResponseFactory.messageSource = new ResourceBundleMessageSource();
        ResponseFactory.messageSource.setBasename("message");
        ResponseFactory.messageSource.setDefaultEncoding("UTF-8");
        ResponseFactory.messageSource.setUseCodeAsDefaultMessage(true);
    }

    public static String getMessage(String code, Object... args){
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }

}
