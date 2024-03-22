//package com.example.demo.config;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
//
//import java.util.Locale;
//
//@Configuration
//public class LocaleChangeResolver extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {
//
//    @Override
//    public Locale resolveLocale(HttpServletRequest request) {
//        String headerLang = request.getHeader("Accept-Language");
//        return StringUtils.isBlank(headerLang) ? Locale.getDefault() : new Locale(headerLang);
//    }
//}