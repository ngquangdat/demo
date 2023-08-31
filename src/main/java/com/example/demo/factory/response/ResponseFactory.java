package com.example.demo.factory.response;

import com.example.demo.constant.ResponseStatusConstant;
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

    public static GeneralResponse<Object> success(Object data){
        GeneralResponse response = new GeneralResponse();
        response.setStatus(getStatus(ResponseStatusConstant.SUCCESS));
        response.setData(data);
        return response;
    }

    public static GeneralResponse<Object> fail(String code, Object data){
        GeneralResponse response = new GeneralResponse();
        response.setStatus(getStatus(code));
        response.setData(data);
        return response;
    }

    public static String getMessage(String code){
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, locale);
    }

    private static Status getStatus(String code){
        Status status = new Status();
        status.setCode(code);
        status.setMessage(getMessage(code));
        return status;
    }
}
