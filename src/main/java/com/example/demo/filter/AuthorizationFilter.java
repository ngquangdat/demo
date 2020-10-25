package com.example.demo.filter;

import com.example.demo.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Configuration
@Order(2)
public class AuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Value("#{'${not-need-authorization-uri}'.split(',')}")
    private String[] notNeedAuthorizationUri;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        if (isAcceptedUri(requestWrapper.getRequestURI())) {
            filterChain.doFilter(requestWrapper, responseWrapper);
            responseWrapper.copyBodyToResponse();
            return;
        }

        String authorization = requestWrapper.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isEmpty(authorization)) {
            String token = authorization.substring(7);
            if (!jwtService.validateJwtToken(token)) {
                sendError(responseWrapper);
            } else {
                filterChain.doFilter(requestWrapper, responseWrapper);
                responseWrapper.copyBodyToResponse();
            }
        } else {
            sendError(responseWrapper);
        }
    }

    private boolean isAcceptedUri(String uri) {
        for (String acceptedUri : notNeedAuthorizationUri) {
            if (uri.contains(acceptedUri)) {
                return true;
            }
        }
        return false;
    }

    private void sendError(ContentCachingResponseWrapper responseWrapper) {
        try {
            responseWrapper.setStatus(HttpStatus.FORBIDDEN.value());
            responseWrapper.setContentType("application/json; charset=utf-8");
            responseWrapper.copyBodyToResponse();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
