package com.pm.fireauthsdk.interceptor;

import com.pm.fireauthsdk.Authorized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Configuration
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final InterceptorHelper helper;

    public AuthorizationInterceptor(InterceptorHelper helper) {
        this.helper = helper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            Authorized annotation = handlerMethod.getMethod().getAnnotation(Authorized.class);
            if (annotation != null) {
                String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
                if (auth == null || auth.isBlank()) {
                    response.setStatus(401);
//                    response.getWriter().write("Unauthorized user");
                    return false;
                }
                String token = auth.split(" ")[1];

                return helper.customAuthorizationCheck(annotation.role(), token, response);
            }
        }
        //d ko yout tl so yin means not annotated, so pass
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // post handle haven't implement yet
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // after completion ll tutu pl haven't implement
    }
}
