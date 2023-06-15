package com.pm.fireauthsdk.interceptor;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.pm.fireauthsdk.Authorized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class InterceptorHelper {

   private final FirebaseAuth firebaseAuth;

    public InterceptorHelper(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public boolean customAuthorizationCheck(String[] role, String token, HttpServletResponse response) throws IOException {
        List<String> roleList;
        try {
            FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token);
            if (role.length > 0) {
                Object firebaseRoles = firebaseToken.getClaims().get("role");
                if (firebaseRoles == null) {
                    response.setStatus(403);
                    // just throw exception and catch with advice if you want to get custom error response
                    // because this writer.write ka stirng kyee pyan tr, obj lote phoe so shote tl response.write.bytearray blah blah
//                    response.getWriter().write("Forbidden access");
                    return false;
                }
                try {
                    roleList = (List<String>) firebaseToken.getClaims().get("role");
                } catch (Exception e) {
                    response.setStatus(400);
//                    response.getWriter().write("Roles not in list format");
                    return false;
                }
                for (String userRolesInFirebase : roleList) {
                    for (String appliedRole : role) {
                        if (userRolesInFirebase.equals(appliedRole)) {
                            return true;
                        }
                    }
                }
                response.setStatus(403);
//                response.getWriter().write("Forbidden access");
                return false;
            }
            return true;
        } catch (FirebaseAuthException ex) {
            log.error("Wrong Token. Cause => {}", ex.getMessage());
            response.setStatus(401);
//            response.getWriter().write("Unauthorized user");
            return false;
        }
    }

    public boolean isAuthorizedAnnotated(HandlerMethod method) {
        Authorized annotation = method.getMethod().getAnnotation(Authorized.class);
        return annotation != null;
    }
}
