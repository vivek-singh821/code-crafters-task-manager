
package com.example.taskmanager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String path = request.getRequestURI();

        if (path.equals("/login")
                || path.equals("/register")
                || path.startsWith("/css/")
                || path.startsWith("/js/")) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
