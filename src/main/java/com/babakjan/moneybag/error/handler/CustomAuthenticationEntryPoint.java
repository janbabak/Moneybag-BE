package com.babakjan.moneybag.error.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        System.out.println("problem is: " + response.getStatus());
        //don't want to override response from CustomAccessDeniedHandler
        if (!response.getHeaderNames().contains("access_denied_reason")
                && response.getStatus() != HttpStatus.FORBIDDEN.value()) {
            response.setHeader("access_denied_reason", "authentication_required");
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
    }
}
