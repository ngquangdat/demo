package com.example.demo.filter;

import com.example.demo.constant.ResponseStatusConstant;
import com.example.demo.factory.response.GeneralResponse;
import com.example.demo.factory.response.ResponseFactory;
import com.example.demo.factory.response.Status;
import com.example.demo.service.JwtService;
import com.example.demo.util.TextUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
@Configuration
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
        if (StringUtils.isNotBlank(authorization)) {
            String token = authorization.substring(7);
            String validate = jwtService.validateJwtToken(token);
            if (validate != null) {
                sendError(responseWrapper, validate);
            } else {
                filterChain.doFilter(requestWrapper, responseWrapper);
                responseWrapper.copyBodyToResponse();
            }
        } else {
            sendError(responseWrapper, ResponseFactory.getMessage(ResponseStatusConstant.NOT_AUTH));
        }
    }

    private boolean isAcceptedUri(String uri) {
//        for (String acceptedUri : notNeedAuthorizationUri) {
//            if (uri.contains(acceptedUri)) {
//                return true;
//            }
//        }
//        return false;
        return true;
    }

    private void sendError(ContentCachingResponseWrapper responseWrapper, String messager) {
        try {
            responseWrapper.setStatus(HttpStatus.FORBIDDEN.value());
            responseWrapper.setContentType("application/json; charset=utf-8");
            responseWrapper.setCharacterEncoding("UTF-8");
            GeneralResponse<Object> response = new GeneralResponse<>();
            Status status = new Status();
            status.setCode(ResponseStatusConstant.NOT_AUTH);
            status.setMessage(messager);
            response.setStatus(status);

            responseWrapper.getWriter().print(TextUtil.gson.toJson(response));
            responseWrapper.copyBodyToResponse();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
